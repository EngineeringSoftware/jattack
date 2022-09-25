package sketchy.driver;

import com.github.javaparser.ast.CompilationUnit;
import org.csutil.checksum.WrappedChecksum;
import sketchy.Config;
import sketchy.annotation.Argument;
import sketchy.annotation.Entry;
import sketchy.ast.Node;
import sketchy.bytecode.StaticFieldAnalyzer;
import sketchy.bytecode.VariableAnalyzer;
import sketchy.compiler.CompilationException;
import sketchy.compiler.InMemoryCompiler;
import sketchy.data.Data;
import sketchy.log.Log;
import sketchy.transformer.HoleExtractor;
import sketchy.transformer.HoleIdAssigner;
import sketchy.transformer.OnDemandTransformer;
import sketchy.transformer.OutputTransformer;
import sketchy.util.IOUtil;
import sketchy.util.Rand;
import sketchy.util.TypeUtil;
import sketchy.util.UniqueList;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
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
     * Determine if sketchy apis are invoked from this driver. If not,
     * an exception would be thrown when eval() is invoked.
     */
    public static boolean isDriven = false;

    /**
     * In-memory compiler we use to compile and load sketches.
     */
    public static final InMemoryCompiler compiler = new InMemoryCompiler();

    /*---------------------- Internal variables. -------------------*/

    // cannot change after init
    private static Map<String, Object> initialFieldValues;
    private static Set<String> allClassNamesInSketch; // including all nested classes
    private static String sketchClzSimpleName;
    private static OutputTransformer outputTransformer;
    private static OnDemandTransformer onDemandTransformer;
    private static ClassLoader initialClassLoader;
    private static String[] argumentMethodNames;
    private static WrappedChecksum checksum; // for testing, only used when Config.mimicExecution is on

    // can change after init
    private static Class<?> sketchClz;
    private static Method entryMethod;

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
        isDriven = true;
        sketchClzSimpleName = TypeUtil.getSimpleName(Config.sketchClzFullName);

        // TODO: Check @Entry and @Argument are used correctly in the
        //  template.

        // Init argumentMethodNames
        try {
            Class<?> fakeClz = Class.forName(Config.sketchClzFullName);
            setArgumentMethodNames(fakeClz);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Assign identifiers for holes
        HoleIdAssigner assigner = new HoleIdAssigner(
                Config.sketchSrc,
                sketchClzSimpleName,
                Config.optSolverAid);
        if (Config.isProfiling) {
            long beg = System.currentTimeMillis();
            assigner.transform();
            totalTransformTime += System.currentTimeMillis() - beg;
        } else {
            assigner.transform();
        }
        CompilationUnit cu = assigner.getCu();

        // Set totalHoles
        Data.setTotalHoles(assigner.getIds());

        // Instantiate transformers
        if (!Config.staticGen
                && (Config.optHotFilling || Config.optSolverAid)) {
            onDemandTransformer = new OnDemandTransformer(
                    cu, Config.optHotFilling, Config.optSolverAid);
        }
        String entryMethodName = assigner.getEntryMethodName();
        boolean entryMethodReturnsVoid = assigner.getEntryMethodReturnsVoid();
        outputTransformer = new OutputTransformer(
                cu, sketchClzSimpleName, entryMethodName,
                entryMethodReturnsVoid, Arrays.asList(argumentMethodNames));

        // Write files
        if (Config.saveHoleValues) {
            outputHoleValuesFileHeader();
        }
    }

    private static void drive() {
        if (Data.thisSketchHasHole()) {
            // Normal generation from a sketch with hole(s)
            if (Config.staticGen) {
                staticGenFromSketch();
            } else {
                genFromSketch();
            }
        } else {
            // No hole exercised
            outputWhenNoHoleExercised();
        }
    }

    private static void staticGenFromSketch() {
        staticLoadSketch();
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
    private static void staticLoadSketch() {
        try {
            // Compile and load sketch classes
            ClassLoader cl = compiler.compileAndGetLoader(
                    Config.sketchClzFullName,
                    outputTransformer.getOrigCu().toString(),
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
            allClassNamesInSketch = compiler.getCompiledClassNames();
            Set<Class<?>> allClzes = TypeUtil.loadClzes(allClassNamesInSketch, true, cl);
            new StaticFieldAnalyzer(allClzes).staticAnalyze();
        } catch (CompilationException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createASTOfHoleExps() {
        String code = new HoleExtractor(outputTransformer.getOrigCu())
                .transformAndGetSrcCode();
        try {
            ClassLoader cl = compiler.compileAndGetLoader(
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
        // transform sketchy apis and output the generated program
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
     * Output a special generated program without transforming hole
     * apis.
     */
    private static void outputNoTransformApi() {
        String outputClzName = Config.outputWOTransformedClzName;
        String code = outputTransformer.setOutClzName(outputClzName)
                .transformAndGetSrcCode();
        outputJavaFile(outputClzName, code);
    }

    /**
     * Output a special generated program with suffix 0, including two
     * cases: 1) there is no hole in the sketch; 2) there is some hole
     * in the sketch but no hole will be reached when the entry method
     * is executed.
     */
    private static void outputWhenNoHoleExercised() {
        String outputClzName = getOutputClzName(0);
        String code = outputTransformer.setOutClzName(outputClzName)
                .transformAndGetSrcCode();
        outputJavaFile(outputClzName, code);
    }

    private static void genFromSketch() {
        try {
            loadSketch();
            do {
                setUp();
                runSketch();
            } while (!tearDown());
        } catch (ClassNotFoundException
                | IllegalAccessException
                | InvocationTargetException
                | NoSuchFieldException
                | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static void loadSketch()
            throws ClassNotFoundException, IllegalAccessException {
        try {
            initialClassLoader = compiler.compileAndGetLoader(
                    Config.sketchClzFullName,
                    outputTransformer.getOrigCu().toString());
        } catch (CompilationException e) {
            // Compilation error in initial loading says the template
            // has issues.
            throw new RuntimeException(e);
        }
        allClassNamesInSketch = compiler.getCompiledClassNames();
        saveInitialStatus();
    }

    private static void runSketch()
            throws NoSuchFieldException, ClassNotFoundException,
            InvocationTargetException, IllegalAccessException,
            NoSuchMethodException {
        try {
            runSketch0();
        } catch (CompilationException e) {
            // This generated program cannot compile so we
            // should drop it.
            // TODO: cache this generated program so we can
            //  know early if we generate it again
            hasCompilingIssueInHotFilling = true;
        }
    }

    private static void runSketch0()
            throws IllegalAccessException, InvocationTargetException,
            NoSuchFieldException, ClassNotFoundException,
            CompilationException, NoSuchMethodException {
        if (Config.mimicExecution) {
            checksum = new WrappedChecksum();
        }
        int prevNHolesFilled = 0;
        Map<String, Object> currFieldValues = new HashMap<>();
        for (int i = 0; i < Config.nInvocations; i++) {
            Log.debug("# iteration " + (i + 1));
            if (Config.optHotFilling || Config.optSolverAid) {
                prevNHolesFilled = Data.getNumFilledHoles();
            }
            if (Config.isProfiling) {
                numIterationsPerGen += 1;
            }

            // Execute the entry method
            executeEntryMethod();

            // We want to execute with full iterations when we turn on
            // Config.mimicExecution for testing; thus we skip all
            // break.
            if (!Config.mimicExecution) {
                if (noHoleRemaining()) {
                    // We stop as early as all the holes have been filled.
                    break;
                }

                if (Config.optStopEarly && TypeUtil.captureStatusAndEquals(
                        sketchClz, initialFieldValues.keySet(), currFieldValues)) {
                    // We stop even earlier if we see a status that is
                    // previously seen.
                    break;
                }
            }

            if ((Config.optHotFilling || Config.optSolverAid)
                    && Data.getNumFilledHoles() > prevNHolesFilled) {
                // Need update currFieldValues if not updated because
                // optStopEarly is off or mimicExecution is on
                if (Config.mimicExecution || !Config.optStopEarly) {
                    currFieldValues = getCurrentStatus();
                }
                // transform known holes and compile in memory in
                // order to speed up the following iterations.
                transformSketchAndCompileInMemory(currFieldValues);
                if (Config.isProfiling) {
                    numHotFillingPerGen +=1;
                }
            }
        }
        if (Config.mimicExecution) {
            checksum.updateStaticFieldsOfClass(sketchClz);
        }
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
     * Wrap {@link Driver#executeEntryMethod0()} to help with
     * profiling.
     */
    private static void executeEntryMethod()
            throws InvocationTargetException, IllegalAccessException,
            NoSuchMethodException {
        if (Config.isProfiling) {
            long beg = System.currentTimeMillis();
            executeEntryMethod0();
            totalExecTime += System.currentTimeMillis() - beg;
        } else {
            executeEntryMethod0();
        }
    }

    /**
     * Execute the entry method using reflection.
     */
    private static void executeEntryMethod0()
            throws InvocationTargetException, IllegalAccessException,
            NoSuchMethodException {
        Method[] argMethods = getArgumentMethods(sketchClz);
        try {
            Object[] argValues = execArgMethodsAndGetValues(argMethods);
            Object ret = entryMethod.invoke(null, argValues);
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
                // e.printStackTrace();
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
            NoSuchFieldException {
        Data.runCount += 1;
        Data.resetStrCache();
        Data.holeVector = new UniqueList<>();
        Data.resetNeverReachableHoles();

        // reload the original class
        reloadSketch(initialClassLoader);
        recoverInitialStatus();

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

    private static void transformSketchAndCompileInMemory(Map<String, Object> fieldValues)
            throws NoSuchFieldException, IllegalAccessException,
            ClassNotFoundException, CompilationException {
        String code = transformOnDemand();
        ClassLoader cl = compiler.compileAndGetLoader(Config.sketchClzFullName, code);
        reloadSketch(cl);
        recoverStatus(fieldValues);
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
     * Return the current status of the sketch class.
     */
    private static Map<String, Object> getCurrentStatus()
            throws NoSuchFieldException, IllegalAccessException {
        if (Config.isProfiling) {
            long beg = System.currentTimeMillis();
            Map<String, Object> status =
                    TypeUtil.captureSpecifiedStatus(sketchClz, initialFieldValues.keySet());
            totalTrackStatusTime += System.currentTimeMillis() - beg;
            return status;
        } else {
            return TypeUtil.captureSpecifiedStatus(sketchClz, initialFieldValues.keySet());
        }
    }

    /**
     * Recover the given status for the sketch class.
     */
    private static void recoverStatus(Map<String, Object> fieldValues)
            throws NoSuchFieldException, IllegalAccessException {
        if (Config.isProfiling) {
            long beg = System.currentTimeMillis();
            TypeUtil.recoverStatus(sketchClz, fieldValues);
            totalTrackStatusTime += System.currentTimeMillis() - beg;
        } else {
            TypeUtil.recoverStatus(sketchClz, fieldValues);
        }
    }

    /**
     * Capture the initial status of the sketch class.
     */
    private static void saveInitialStatus()
            throws IllegalAccessException, ClassNotFoundException {
        Class<?> initialClz = TypeUtil.loadClz(Config.sketchClzFullName, true, initialClassLoader);
        if (Config.isProfiling) {
            long beg = System.currentTimeMillis();
            initialFieldValues = TypeUtil.captureStatus(initialClz);
            totalTrackStatusTime += System.currentTimeMillis() - beg;
        } else {
            initialFieldValues = TypeUtil.captureStatus(initialClz);
        }
    }

    /**
     * Recover the initial status of the sketch class.
     */
    private static void recoverInitialStatus()
            throws IllegalAccessException, NoSuchFieldException {
        if (Config.isProfiling) {
            long beg = System.currentTimeMillis();
            TypeUtil.recoverStatus(sketchClz, initialFieldValues);
            totalTrackStatusTime += System.currentTimeMillis() - beg;
        } else {
            TypeUtil.recoverStatus(sketchClz, initialFieldValues);
        }
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

        // transform sketchy apis and output the generated program
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
            IOUtil.writeToFile(Config.outputDir, outputClzName + "_output.txt", checksum.getValue() + "\n");
            outputJavaFile(outputClzName, code);
        } else if (canOutput) {
            outputJavaFile(outputClzName, code);outputJavaFile(outputClzName, code);
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

    private static void reloadSketch(ClassLoader cl)
            throws ClassNotFoundException {
        reloadSketch(cl, false);
    }

    private static void reloadSketch(ClassLoader cl, boolean initialize)
            throws ClassNotFoundException {
        TypeUtil.loadClzes(allClassNamesInSketch, initialize, cl);
        sketchClz = TypeUtil.loadClz(Config.sketchClzFullName, initialize, cl);
        entryMethod = getEntryMethod(sketchClz);
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

    private static void setArgumentMethodNames(Class<?> clz) {
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
        argumentMethodNames = argMethods.stream().map(Method::getName)
                .toArray(String[]::new);
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
        return sketchClzSimpleName + Config.outputClzNamePostfix + idx;
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
        StringBuilder sb = new StringBuilder();
        sb.append("invalidArrayIndexException\n")
                .append(numInvalidArrIdxException).append("\n");
        IOUtil.writeToFile(Config.outputDir,
                Config.invalidArrIdxExceptionFile,
                sb.toString());
    }
}
