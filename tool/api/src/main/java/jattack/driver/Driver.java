package jattack.driver;

import com.github.javaparser.ast.CompilationUnit;
import jattack.Config;
import jattack.Constants;
import jattack.annotation.Argument;
import jattack.annotation.Arguments;
import jattack.annotation.Entry;
import jattack.ast.Node;
import jattack.bytecode.StaticFieldAnalyzer;
import jattack.bytecode.VariableAnalyzer;
import jattack.compiler.ClassBytes;
import jattack.compiler.CompilationException;
import jattack.compiler.InMemoryCompiler;
import jattack.data.Data;
import jattack.log.Log;
import jattack.transformer.HoleExtractor;
import jattack.transformer.HoleIdAssigner;
import jattack.transformer.OnDemandTransformer;
import jattack.transformer.OutputTransformer;
import jattack.util.IOUtil;
import jattack.util.Rand;
import jattack.util.TypeUtil;
import jattack.util.UniqueList;
import org.csutil.checksum.WrappedChecksum;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Entry class.
 * TODO: use an instance instead of static.
 */
public class Driver {

    /**
     * Random generator used in random search strategy.
     */
    public static Rand rand = new Rand();

    /**
     * Determine if jattack apis are invoked from this driver. If not,
     * an exception would be thrown when eval() is invoked.
     */
    public static boolean isDriven = false;

    /**
     * In-memory compiler to compile and load templates.
     */
    public static InMemoryCompiler compiler;

    /*---------------------- Internal variables. -------------------*/

    // cannot change after init
    private static Class<?> tmplClz;
    private static Method entryMethod;
    private static boolean entryMethodIsStatic;
    private static Method[] argMethods; // separate @Argument methods
    private static Method argsMethod; // single @Arguments method
    private static String initialTmplSrcCode;
    private static ClassBytes initialTmplClassBytes;
    private static Map<String, Map<String, Object>> immutableStaticFieldInitialValues; // {class name, {field name, field value}}
    private static Set<String> allClassNamesInTmpl; // including all nested classes
    private static Set<Class<?>> allClzes; // including all nested classes
    private static String tmplClzFullName;
    private static String tmplClzSimpleName;
    private static String[] argumentMethodNames;
    private static String argsMethodName;
    private static OutputTransformer outputTransformer;
    private static OnDemandTransformer onDemandTransformer;
    private static WrappedChecksum checksum; // for testing, only used when Config.mimicExecution is on

    // flags per gen
    private static boolean hasCompilingIssueInHotFilling;

    /* Profiling. */
    private static long totalExecTime = 0;
    public static long totalCompileTime = 0;
    private static long totalTransformTime = 0;
    private static long totalTrackStatusTime = 0;
    private static long numIterationsPerGen = 0;
    private static long numTotalIterations = 0;
    private static long numHotFillingPerGen = 0;
    private static long numTotalHotFilling = 0;

    /* Research. */

    /**
     * The number of all the conditions that have been evaluated.
     */
    private static long numAllConds = 0;

    /**
     * The number of conditions that have been evaluated to true all
     * the time and have been evaluated to false all the time.
     */
    private static final long[] numConstConds = new long[] {0, 0};

    /* The number of exceptions. */
    private static boolean threwInvalidArrIdxExceptionPerGen = false;
    private static int numInvalidArrIdxException = 0;

    /**
     * Main method.
     */
    public static void main(String[] args) {
        init(args);
        drive();
        terminate();
    }

    /**
     * Finalize work.
     */
    private static void terminate() {
        // Output profiling data
        if (Config.isProfiling) {
            outputProfilingFile();
        }
        if (Config.countInvalidArrIdxException) {
            outputInvalidArrIdxExceptionFile();
        }
    }

    /**
     * Initialize fields.
     */
    private static void init(String[] args) {
        Cli.parseArgs(args);
        compiler = new InMemoryCompiler();
        isDriven = true;
        tmplClzFullName = Config.tmplClzFullName;
        tmplClzSimpleName = TypeUtil.getSimpleName(tmplClzFullName);

        // TODO: Check @Entry, @Argument, @Arguments are used
        //  correctly in the template.

        // Init argumentMethodNames
        try {
            Class<?> clz = TypeUtil.loadClz(tmplClzFullName, false);
            argumentMethodNames = getArgumentMethodNames(clz);
            argsMethodName = getArgsMethodName(clz);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Assign identifiers for holes
        HoleIdAssigner assigner = new HoleIdAssigner(
                Config.tmplSrcPath,
                tmplClzSimpleName,
                Config.optSolverAid);
        if (Config.isProfiling) {
            long beg = System.currentTimeMillis();
            assigner.transform();
            totalTransformTime += System.currentTimeMillis() - beg;
        } else {
            assigner.transform();
        }
        CompilationUnit cu = assigner.getCu();
        initialTmplSrcCode = cu.toString();


        // Set totalHoles
        Data.setTotalHoles(assigner.getIds());

        // Instantiate transformers
        if (!Config.staticGen
                && (Config.optHotFilling || Config.optSolverAid)) {
            onDemandTransformer = new OnDemandTransformer(
                    cu, Config.optHotFilling, Config.optSolverAid);
        }
        String entryMethodName = assigner.getEntryMethodName();
        String[] entryMethodParamTypes = assigner.getEntryMethodParamTypes();
        boolean entryMethodReturnsVoid = assigner.getEntryMethodReturnsVoid();
        entryMethodIsStatic = assigner.getEntryMethodIsStatic();
        outputTransformer = new OutputTransformer(
                cu, tmplClzSimpleName, entryMethodName, entryMethodParamTypes,
                entryMethodReturnsVoid, entryMethodIsStatic,
                argumentMethodNames, argsMethodName);

        // Write files
        if (Config.saveHoleValues) {
            outputHoleValuesFileHeader();
        }
    }

    private static void drive() {
        if (Data.thisTmplHasHole()) {
            // Normal generation from a template with hole(s)
            if (Config.staticGen) {
                staticGenFromTmpl();
            } else {
                genFromTmpl();
            }
        } else {
            // No hole in this template
            outputWhenNoHoleExercised();
        }
    }

    private static void staticGenFromTmpl() {
        staticLoadTmpl();
        createASTOfHoleExps();
        do {
            Data.runCount += 1;
            Data.resetStrCache();
            // For each exp node just invoke staticEval
            for (Map.Entry<Integer, Node<?>> e : Data.getASTCache().entrySet()) {
                int holeId = e.getKey();
                Node<?> exp = e.getValue();
                exp.staticEval(holeId);
            }
        } while (!staticGenTearDown());
    }

    /**
     * Get choices for every hole, which is mainly analyzing which
     * variables are available at every hole.
     */
    private static void staticLoadTmpl() {
        try {
            // Compile and load template classes
            ClassLoader cl = compiler.compileAndRedefine(
                    tmplClzFullName,
                    initialTmplSrcCode,
                    false);
            // Get local variables available for every hole populating
            // {@code Data.varsByHole}.
            for (Map.Entry<String, byte[]> entry : compiler.getClassBytes().entrySet()) {
                String className = entry.getKey();
                byte[] bytes = entry.getValue();
                VariableAnalyzer.staticAnalyze(className, bytes);
            }
            // Get fields available from every hole, also saving into
            // {@code Data.varsByHole}.
            allClassNamesInTmpl = compiler.getCompiledClassNames();
            allClzes = TypeUtil.loadClzes(allClassNamesInTmpl, true, cl);
            new StaticFieldAnalyzer(allClzes).staticAnalyze();
        } catch (CompilationException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createASTOfHoleExps() {
        String code = new HoleExtractor(outputTransformer.getOrigCu())
                .transformAndGetSrcCode();
        try {
            ClassLoader cl = compiler.compileAndRedefine(
                    HoleExtractor.CLASS_NAME, code, false);
            Class<?> clz = TypeUtil.loadClz(HoleExtractor.CLASS_NAME, false, cl);
            Method m = clz.getDeclaredMethod(HoleExtractor.METHOD_NAME);
            // By running this method we populate Data.astCache
            m.invoke(null);
        } catch (CompilationException
                | ClassNotFoundException
                | NoSuchMethodException
                | IllegalAccessException
                | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean staticGenTearDown() {
        if (!Data.saveToPastStrCache()) {
            // Repeat skipping this output
            Data.repeatedTrials += 1;
            return Data.isDone();
        }
        // transform jattack apis and output the generated program
        try {
            transformAndOutput(); // throws CompilationException
        } catch (CompilationException e) {
            // count compiling failures as repeated trials,
            // skip this output
            Data.repeatedTrials += 1;
            return Data.isDone();
        }
        return Data.isDone();
    }

    /**
     * Output a special generated program with suffix 0, including two
     * cases: 1) there is no hole in the template; 2) there is some
     * hole in the template but no hole will be reached when the entry
     * method is executed.
     */
    private static void outputWhenNoHoleExercised() {
        String outputClzName = getOutputClzName(0);
        String code = outputTransformer.setOutClzName(outputClzName)
                .transformAndGetSrcCode();
        outputJavaFile(outputClzName, code);
    }

    private static void genFromTmpl() {
        try {
            prepareForRunningTmpl();
            do {
                setUp();
                runTmpl();
            } while (!tearDown());
        } catch (ClassNotFoundException
                | IllegalAccessException
                | InvocationTargetException
                | NoSuchFieldException
                | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static void prepareForRunningTmpl()
            throws ClassNotFoundException, IllegalAccessException,
            NoSuchMethodException {
        try {
            compiler.compileAndRedefine(tmplClzFullName, initialTmplSrcCode);
        } catch (CompilationException e) {
            throw new RuntimeException(e);
        }
        initialTmplClassBytes = compiler.getClassBytes();
        allClassNamesInTmpl = compiler.getCompiledClassNames();
        tmplClz = TypeUtil.loadClz(tmplClzFullName);
        allClzes = TypeUtil.loadClzes(allClassNamesInTmpl);
        entryMethod = getEntryMethod(tmplClz);
        argMethods = getArgumentMethods(tmplClz);
        argsMethod = getArgsMethod(tmplClz);
        saveInitialTmplStatus();
    }

    private static void recoverInitialTmplClass() {
        compiler.redefine(initialTmplClassBytes);
    }

    private static void runTmpl()
            throws NoSuchFieldException, ClassNotFoundException,
            InvocationTargetException, IllegalAccessException,
            NoSuchMethodException {
        try {
            runTmpl0();
        } catch (CompilationException e) {
            // This generated program cannot compile so we
            // should drop it.
            // TODO: cache this generated program so we can
            //  know early if we generate it again
            hasCompilingIssueInHotFilling = true;
        }
    }

    private static void runTmpl0()
            throws IllegalAccessException, InvocationTargetException,
            NoSuchFieldException, CompilationException {
        if (Config.mimicExecution) {
            checksum = new WrappedChecksum(Config.ignoreJDKClasses);
        }
        int prevNHolesFilled = 0;
        Object[] argsIncludingReceiverIfExists = execAndGetArgValues();
        Object receiver = entryMethodIsStatic ? null : argsIncludingReceiverIfExists[0];
        Object[] args = entryMethodIsStatic ?
                argsIncludingReceiverIfExists :
                Arrays.copyOfRange(argsIncludingReceiverIfExists, 1, argsIncludingReceiverIfExists.length);
        long initialState = hashState(receiver, args);
        Set<Long> seenStates = new HashSet<>();
        seenStates.add(initialState);
        Log.debug("initial state: " + initialState);
        for (int i = 0; i < Config.nInvocations; i++) {
            Log.debug("# iteration " + (i + 1));
            if (Config.optHotFilling || Config.optSolverAid) {
                prevNHolesFilled = Data.getNumFilledHoles();
            }
            if (Config.isProfiling) {
                numIterationsPerGen += 1;
            }

            // Execute the entry method
            executeEntryMethod(receiver, args);

            // We want to execute with full iterations when we turn on
            // Config.mimicExecution for testing; thus we skip all
            // break.
            if (!Config.mimicExecution) {
                if (noHoleRemaining()) {
                    // We stop as early as all the holes have been filled.
                    break;
                }

                // TODO: static fields of all the classes in the world
                Log.debug("# " + (i + 1) + " state: " + hashState(receiver, args));
                if (Config.optStopEarly && !seenStates.add(hashState(receiver, args))) {
                    // We stop even earlier if we see a status we have
                    // seen.
                    break;
                }
            }

            if ((Config.optHotFilling || Config.optSolverAid)
                    && Data.getNumFilledHoles() > prevNHolesFilled) {
                // Transform known holes and compile in memory in
                // order to speed up the following iterations.
                transformTmplAndCompileInMemoryAndRedefineClass();
                if (Config.isProfiling) {
                    numHotFillingPerGen +=1;
                }
            }
        }
        if (Config.mimicExecution) {
            checksum.updateStaticFieldsOfClass(tmplClz);
        }
    }

    private static long hashState(Object receiver, Object[] args) {
        WrappedChecksum cs = new WrappedChecksum(Config.ignoreJDKClasses);
        allClzes.forEach(cs::updateStaticFieldsOfClass);
        cs.update(receiver);
        cs.update(args);
        return cs.getValue();
    }

    /**
     * Gets the first non-java own class from the given stack trace.
     * Returns null if no such class is found.
     */
    private static StackTraceElement getFirstNonJavaOwnStack(StackTraceElement[] stackTrace) {
        for (StackTraceElement frame : stackTrace) {
            String className = frame.getClassName();
            if (!TypeUtil.isJavaOwnClass(className)) {
                return frame;
            }
        }
        return null;
    }

    /**
     * Wrap {@link Driver#executeEntryMethod0(Object, Object...)} to
     * help with profiling.
     */
    private static void executeEntryMethod(Object receiver, Object... args)
            throws InvocationTargetException, IllegalAccessException {
        if (Config.isProfiling) {
            long beg = System.currentTimeMillis();
            executeEntryMethod0(receiver, args);
            totalExecTime += System.currentTimeMillis() - beg;
        } else {
            executeEntryMethod0(receiver, args);
        }
    }

    /**
     * Execute the entry method using reflection. receiver should be
     * null for a static entry method.
     */
    private static void executeEntryMethod0(Object receiver, Object... args)
            throws InvocationTargetException, IllegalAccessException {
        try {
            Object ret = entryMethod.invoke(receiver, args);
            if (Config.mimicExecution
                    && !entryMethod.getReturnType().equals(Void.TYPE)) {
                // We don't checksum anything in a generated program
                // if the entry method returns void (see
                // OutputTransformer), so we want to keep consistent
                // here.
                checksum.update(ret);
            }
        } catch (InvocationTargetException e) {
            // Throw the exception only when it is caused by the tool
            // itself and not an arithmetic exception or invalid index
            // exception
            Throwable cause = e.getCause();
            if (Config.countInvalidArrIdxException && isInvalidArrayIndexException(cause)) {
                threwInvalidArrIdxExceptionPerGen = true;
            }
            StackTraceElement[] frames = cause.getStackTrace();
            if (frames.length == 0) {
                // -XX:+OmitStackTraceInFastThrow occurred; we expect
                // this is a pre-existing exception, and we can safely
                // ignore it, because we know we already saw and
                // ignored it otherwise the program would have stopped.
                return;
            }

            // Classloader issue should be gone now; tested it was
            // gone. this is not supposed to be triggered any more but
            // keep it for safe.
            if (cause instanceof IllegalAccessError) {
                throw e;
            }

            StackTraceElement frame = getFirstNonJavaOwnStack(frames);
            if (frame == null) {
                // exception is from inside Java
                throw e;
            }
            // We eventually want to remove the constraint of
            // !canIgnore here; for now I keep it to learn what
            // interesting exception we could be handling; otherwise
            // any exception just slips before we are aware of it.
            if (TypeUtil.isToolOwnClass(frame.getClassName()) && !canIgnore(cause)) {
                throw e;
            } else {
                // Ignore
                // TODO: perform the same checksum as we do in main
                //   generated by output transformer for testing
                //   purpose
                if (Log.getLevel().getOrder() >= Log.Level.DEBUG.getOrder()) {
                    e.printStackTrace();
                }
                if (Config.mimicExecution) {
                    checksum.update(cause.getClass().getName());
                }
            }
        }
    }

    /**
     * Set up work before each run of the entry method.
     */
    private static void setUp()
            throws IllegalAccessException, ClassNotFoundException,
            NoSuchFieldException, NoSuchMethodException,
            InvocationTargetException {
        // TODO: Suggests GC recycle some memory otherwise we will
        //   have OutOfMemoryError some times, but this is not the
        //   best way.
        System.gc();

        Data.runCount += 1;
        Data.resetStrCache();
        Data.holeVector = new UniqueList<>();
        Data.resetNeverReachableHoles();

        // recover the original class, both definition and state
        recoverInitialTmplClass();
        recoverInitialTmplStatus();

        // reset inner state of onDemandTransformer
        if (Config.optHotFilling || Config.optSolverAid) {
            onDemandTransformer.resetCu();
            if (Config.optSolverAid) {
                Data.resetAlwaysTrueOrFalseCondHoles();
            }
        }

        // reset flags.
        hasCompilingIssueInHotFilling = false;

        // reset profiling counters
        resetRuntimeStatsCounters();
    }

    private static void transformTmplAndCompileInMemoryAndRedefineClass()
            throws CompilationException {
        String code = transformOnDemand();
        compiler.compileAndRedefine(tmplClzFullName, code);
    }

    private static String transformOnDemand() {
        if (Config.isProfiling) {
            long beg = System.currentTimeMillis();
            String code = onDemandTransformer.transformAndGetSrcCode();
            totalTransformTime += System.currentTimeMillis() - beg;
            return code;
        } else {
            return onDemandTransformer.transformAndGetSrcCode();
        }
    }

    /**
     * Capture the initial status of the template class.
     */
    private static void saveInitialTmplStatus() throws IllegalAccessException {
        if (Config.isProfiling) {
            long beg = System.currentTimeMillis();
            saveInitialTmplStatus0();
            totalTrackStatusTime += System.currentTimeMillis() - beg;
        } else {
            saveInitialTmplStatus0();
        }
    }

    private static void saveInitialTmplStatus0() throws IllegalAccessException {
        // Save field values of every class defined from template source
        // file.
        immutableStaticFieldInitialValues = new HashMap<>();
        for (Class<?> clz : allClzes) {
            immutableStaticFieldInitialValues.put(clz.getName(), TypeUtil.captureImmutableStaticFieldValues(clz));
        }
    }

    /**
     * Recover the initial status of the template class by invoking
     * the special method that copies <clinit> we inserted.
     */
    private static void recoverInitialTmplStatus()
            throws IllegalAccessException, NoSuchFieldException,
            NoSuchMethodException, InvocationTargetException {
        // TODO: the order matters we should follow the original order
        //  of the class loaded to re-initialize them.
        for (Class<?> clz : allClzes) {
            recoverInitialStatusForSingleClz(clz);
        }
    }

    private static void recoverInitialStatusForSingleClz(Class<?> clz)
            throws IllegalAccessException, NoSuchFieldException,
            NoSuchMethodException, InvocationTargetException {
        // Restore values for the fields that were not initialized in
        // the static initializer.
        for (Map.Entry<String, Object> e: immutableStaticFieldInitialValues.get(clz.getName()).entrySet()) {
            String name = e.getKey();
            Object val = e.getValue();
            Field f = clz.getDeclaredField(name);
            f.setAccessible(true);
            // primitives or strings could be constants
            // and not set in static initializer, so we have to
            // capture/restore their values manually.
            // e.g., private static final int i = 10;
            f.set(null, val);
        }

        // Then we invoke copied static initializer method (if it
        // exists) to re-initialize some fields that was initialized
        // there.
        if (!Data.hasStaticInitializer(clz.getName())) {
            return;
        }
        // TODO: to recover inheritted static fields which were
        //  initialized by parent class or other dependencies.
        Method m = clz.getDeclaredMethod(Constants.STATIC_INITIALIZER_COPY_METHOD);
        m.setAccessible(true);
        m.invoke(null);
    }

    /**
     * Tear down work after each run of the entry method. Returns true
     * if we are done with exploration (systematic style) or generate
     * sufficient programs (random style).
     */
    private static boolean tearDown() {

        // If no hole is exercised, then we can stop because we will
        // be not able to generate any program.
        if (Data.isNoHoleFilled()) {
            outputWhenNoHoleExercised();
            return true;
        }

        // We don't want duplicated programs generated so we rely on
        // the return value from {@link Data#saveToPastStrCache}
        // to check.
        // For systematic ss, this check is redundant since we will
        // never repeat.
        if (Config.ss != SearchStrategy.SYSTEMATIC
                && !Data.saveToPastStrCache()) {
            // Repeat, skip this output
            Data.repeatedTrials += 1;
            return Data.isDone();
        } else if (Config.ss == SearchStrategy.SYSTEMATIC) {
            // Analyze holeVector to figure out which holes should
            // explore next in the following run
            Data.setFirstHoleIdxThatShouldStepInNextRun();
        }

        // If we already know something is not compilable from
        // previous execution, skip this output
        if (hasCompilingIssueInHotFilling) {
            Data.repeatedTrials += 1;
            return Data.isDone();
        }

        // transform jattack apis and output the generated program
        try {
            transformAndOutput(); // throws CompilationException
            collectRuntimeStats();
        } catch (CompilationException e) {
            // count compiling failures as repeated trials,
            // skip this output
            Data.repeatedTrials += 1;
            return Data.isDone();
        }
        return Data.isDone();
    }

    private static void collectRuntimeStats() {
        if (Config.saveHoleValues) {
            // write hole values saved to file
            appendHoleValuesFile();
        }
        if (Config.isProfiling) {
            numTotalIterations += numIterationsPerGen;
            numTotalHotFilling += numHotFillingPerGen;
        }
        // Process dynamic information collected
        if (Config.dynamicCollecting) {
            numAllConds += Data.getNumAllCondsEvaled();
            int[] nums =  Data.getNumConstConds();
            numConstConds[0] += nums[0];
            numConstConds[1] += nums[1];
        }
        if (Config.countInvalidArrIdxException && threwInvalidArrIdxExceptionPerGen) {
            numInvalidArrIdxException += 1;
        }
    }

    private static void resetRuntimeStatsCounters() {
        // reset profiling counters
        if (Config.isProfiling) {
            numIterationsPerGen = 0;
            numHotFillingPerGen = 0;
        }
        // reset dynamic collection related variables
        if (Config.dynamicCollecting) {
            Data.resetCondValsCollection();
        }
        // reset save hole values related variables
        if (Config.saveHoleValues) {
            Data.resetCondVals();
        }
        // reset exception counters
        if (Config.countInvalidArrIdxException) {
            threwInvalidArrIdxExceptionPerGen = false;
        }
    }

    private static void transformAndOutput() throws CompilationException {
        transformAndOutput(true);
    }
    private static void transformAndOutput(boolean canOutput)
            throws CompilationException {
        int outputIdx = Data.outputCount + 1;
        String outputClzName = getOutputClzName(outputIdx);
        String code;

        // Fill in holes
        if (Config.isProfiling) {
            long beg = System.currentTimeMillis();
            code = outputTransformer.setOutClzName(outputClzName).transformAndGetSrcCode();
            totalTransformTime += System.currentTimeMillis() - beg;
        } else {
            code = outputTransformer.setOutClzName(outputClzName).transformAndGetSrcCode();
        }

        // Check if the generated program is compilable
        if (!Config.allowNonCompilableOutput) {
            // throw a CompilerException if the generated program cannot compile
            compiler.compile(outputClzName, code);
        }

        // Output only if the generated program is able to compile
        if (Config.mimicExecution) {
            // print out checksum value
            IOUtil.writeToFile(Config.outputDir,
                               outputClzName + "_output.txt",
                               checksum.getValue() + "\n");
            outputJavaFile(outputClzName, code);
        } else if (canOutput) {
            outputJavaFile(outputClzName, code);
        }

        Data.outputCount = outputIdx;
        Data.repeatedTrials = 0;
    }

    /**
     * Return true if there is still unfilled hole currently.
     */
    private static boolean noHoleRemaining() {
        return Data.isAllReachableHolesFilled();
    }

    /*-------------------- Helper methods. -------------------------*/

    private static boolean isInvalidArrayIndexException(Throwable e) {
        return e instanceof ArrayIndexOutOfBoundsException
                || e instanceof NegativeArraySizeException;
    }

    private static boolean canIgnore(Throwable e) {
        return isInvalidArrayIndexException(e)
                || e instanceof ArithmeticException;
    }

    private static Method getEntryMethod(Class<?> clz) {
        for (Method method : clz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Entry.class)) {
                method.setAccessible(true);
                return method;
            }
        }
        // There must be one and only one method annotated with @Entry
        // as HoldIdAssigner transformer have checked this in init().
        // Should not reach!
        return null;
    }

    private static Method[] getArgumentMethods(Class<?> clz)
            throws SecurityException, NoSuchMethodException {
        Method[] methods = new Method[argumentMethodNames.length];
        for (int i = 0; i < methods.length; i++) {
            methods[i] = clz.getDeclaredMethod(argumentMethodNames[i]);
            methods[i].setAccessible(true);
        }
        return methods;
    }

    private static String[] getArgumentMethodNames(Class<?> clz) {
        Set<Method> argMethods = new TreeSet<>((m1, m2) -> {
            // Sort argument methods by value in @Argument
            int v1 = m1.getAnnotation(Argument.class).value();
            int v2 = m2.getAnnotation(Argument.class).value();
            return v1 - v2;
        });
        for (Method m : clz.getDeclaredMethods()) {
            if (m.isAnnotationPresent(Argument.class)) {
                argMethods.add(m);
            }
        }
        // get method names
        return argMethods.stream().map(Method::getName)
                .toArray(String[]::new);
    }

    private static Method getArgsMethod(Class<?> clz)
            throws SecurityException, NoSuchMethodException {
        if (argsMethodName == null) {
            // no such method
            return null;
        }
        Method m = clz.getDeclaredMethod(argsMethodName);
        m.setAccessible(true);
        return m;
    }

    private static String getArgsMethodName(Class<?> clz) {
        for (Method m : clz.getDeclaredMethods()) {
            if (m.isAnnotationPresent(Arguments.class)) {
                return m.getName();
            }
        }
        // no such method
        return null;
    }

    private static Object[] execAndGetArgValues()
            throws InvocationTargetException, IllegalAccessException {
        if (argsMethod != null) {
            return execArgsMethodAndGetValues(argsMethod);
        } else {
            return execArgMethodsAndGetValues(argMethods);
        }
    }

    private static Object[] execArgsMethodAndGetValues(Method method)
            throws InvocationTargetException, IllegalAccessException {
        return (Object[]) method.invoke(null);
    }

    private static Object[] execArgMethodsAndGetValues(Method[] methods)
            throws InvocationTargetException, IllegalAccessException {
        Object[] args = new Object[methods.length];
        for (int i = 0; i < methods.length; i++) {
            args[i] = methods[i].invoke(null);
        }
        return args;
    }

    private static String getOutputClzName(int idx) {
        return tmplClzSimpleName + Config.outputClzNamePostfix + idx;
    }

    private static void outputProfilingFile() {
        StringBuilder out = new StringBuilder();

        // Header
        out.append("execTime,compileTime,transformTime,trackStatusTime,numIterations,numHotFilling");
        if (Config.dynamicCollecting) {
            out.append(",numAllConds,numAlwaysTrueConds,numAlwaysFalseConds");
        }
        out.append("\n");

        // Content
        out.append(totalExecTime)
                .append(",").append(totalCompileTime)
                .append(",").append(totalTransformTime)
                .append(",").append(totalTrackStatusTime)
                .append(",").append(numTotalIterations)
                .append(",").append(numTotalHotFilling);
        if (Config.dynamicCollecting) {
            out.append(",").append(numAllConds)
                    .append(",").append(numConstConds[0])
                    .append(",").append(numConstConds[1]);
        }
        out.append("\n");

        IOUtil.writeToFile(Config.outputDir, Config.profilingFile, out.toString());
    }

    private static void outputJavaFile(
            String outputClzName, String code) {
        if (Config.disableOutput) {
            // With outputs disabled, we need to at least print out how
            // many programs we tried.
            IOUtil.writeToFile(Config.outputDir, "output.txt", "outputCount: " + Data.outputCount);
        } else {
            IOUtil.writeToFile(Config.outputDir, outputClzName + ".java", code);
        }
    }

    private static void outputHoleValuesFileHeader() {
        IOUtil.writeToFile(Config.outputDir,
                Config.holeValuesFile,
                "class,holeId,expression,sum,values\n");
    }

    private static void appendHoleValuesFile() {
        IOUtil.writeToFile(Config.outputDir,
                Config.holeValuesFile,
                condValsToString(getOutputClzName(Data.outputCount)),
                true);
    }

    private static String condValsToString(String clzName) {
        StringBuilder out = new StringBuilder();
        for (Map.Entry<Integer, List<Boolean>> cond : Data.getCondVals().entrySet()) {
            int holeId = cond.getKey();
            // TODO: ',' could not be a good delimiter here since
            //  expressions might contain commas.
            out.append(clzName) // class
                    .append(",").append(holeId) // holeId
                    .append(",").append(Data.getJavaStrOfHole(holeId)); // expression

            // Read all the values evaluated
            boolean isFirst = true;
            boolean alwaysSame = true;
            boolean firstVal = false; // initialization is unnecessary just to let javac happy
            StringBuilder valsOut = new StringBuilder();
            for (boolean condVal : cond.getValue()) {
                if (isFirst) {
                    isFirst = false;
                    firstVal = condVal;
                } else {
                    valsOut.append("->");
                    if (firstVal != condVal) {
                        alwaysSame = false;
                    }
                }
                valsOut.append(condVal);
            }

            if (alwaysSame) {
                out.append(",").append(firstVal ? "T" : "F"); // summary of values
                out.append(",").append("NA"); // values
            } else {
                out.append(",").append("V"); // summary of values
                out.append(",").append(valsOut); // values
            }
            out.append("\n");
        }
        return out.toString();
    }

    /**
     * Output the file holding counting of invalid array index
     * exceptions.
     */
    private static void outputInvalidArrIdxExceptionFile() {
        String str = "invalidArrayIndexException\n" +
                numInvalidArrIdxException + "\n";
        IOUtil.writeToFile(Config.outputDir,
                Config.invalidArrIdxExceptionFile,
                str);
    }
}
