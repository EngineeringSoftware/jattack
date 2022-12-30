package jattack;

import jattack.driver.SearchStrategy;

import java.util.Arrays;
import java.util.List;

public class Config {

    /**
     * Search strategy used.
     */
    public static SearchStrategy ss = SearchStrategy.RANDOM;

    public static long seed = 42;

    /**
     * The number of programs as output, enabled if in random style or
     * in systematic style with isExhaustive option off.
     */
    public static int nOutputs = 100;

    /**
     * Control if we exhaustively explore all systematic choices or up
     * to {@link Config#nOutputs} programs generated, enabled if in
     * systematic style.
     */
    public static boolean isExhaustive = false;

    /**
     * The maximum number of times a jitted method is expected to be
     * invoked in main during experiments.
     */
    public static int nInvocations = 100000;

    /**
     * The char space that will be explored, enabled if in
     * systematic style.
     */
    public static List<Character> chars = Arrays.asList(
            'a', Character.MAX_VALUE, Character.MIN_VALUE);

    /**
     * The byte space that will be explored, enabled if in
     * systematic style.
     */
    public static List<Byte> bytes = Arrays.asList(
            (byte) 0, (byte) 1, (byte) -1, Byte.MAX_VALUE, Byte.MIN_VALUE);

    /**
     * The short space that will be explored, enabled if in
     * systematic style.
     */
    public static List<Short> shorts = Arrays.asList(
            (short) 0, (short) 1, (short) -1, Short.MAX_VALUE, Short.MIN_VALUE);

    /**
     * The integer space that will be explored, enabled if in
     * systematic style.
     */
    public static List<Integer> ints = Arrays.asList(
            0, 1, -1, Integer.MAX_VALUE, Integer.MIN_VALUE);

    /**
     * The long integer space that will be explored, enabled if in
     * systematic style.
     */
    public static List<Long> longs = Arrays.asList(
            0L, 1L, -1L, Long.MAX_VALUE, Long.MIN_VALUE);

    /**
     * The float space that will be explored, enabled if in
     * systematic style.
     */
    public static List<Float> floats = Arrays.asList(
            0.0F, 1.0F, -1.0F, Float.MAX_VALUE, Float.MIN_VALUE);
    // Do you know Float.MIN_VALUE is positive :)

    /**
     * The double space that will be explored, enabled if in
     * systematic style.
     */
    public static List<Double> doubles = Arrays.asList(
            0.0, 1.0, -1.0, Double.MAX_VALUE, Double.MIN_VALUE);
    // Do you know Double.MIN_VALUE is positive :)

    /**
     * Directory to output generated programs.
     */
    public static String outputDir = ".jattack";

    /**
     * The postfix of output generated programs' names. A full name
     * looks like
     * {@code tmplClzName + outputClzNamePostfix + outputCount}.
     */
    public static String outputClzNamePostfix = "Gen";

    /* The info of the test template program as input. */
    public static String tmplClzFullName;
    public static String tmplSrcPath; // the path of source code.

    /**
     * Determine if we keep package declarations in generated
     * programs.
     */
    public static boolean keepPkg = true;

    /**
     * The maximum number of trials before stopping trying to generate
     * a new program in random search strategy.
     */
    public static int maxRepeatedTrialsAllowed = 100;

    /**
     * Determine if we allow non-compilable generated programs
     * outputted.
     */
    public static boolean allowNonCompilableOutput = false;

    /*----------- Optimization technique on/off flags. -------------*/

    /**
     * Determine if we enable "hot" filling optimization in a single
     * run.
     */
    public static boolean optHotFilling = true;

    /**
     * Determine if we stop early when seeing no change between two
     * consecutive iterations in a single run.
     */
    public static boolean optStopEarly = true;

    /**
     * Determine if we use Z3 solver to decide if a condition is valid
     * or unsatisfiable and then we dynamically remove this condition
     * or the associated block by in-memory recompiling and reloading.
     */
    public static boolean optSolverAid = true;

    /**
     * Determine if we do "static" generation after we are done with
     * normal generation.
     */
    public static boolean staticGen = false;

    /*-------------------- Track holes -----------------------------*/

    // This is for us to insert probes in generated programs so we can
    // track if every hole is executed.
    public static boolean trackHoles = false;
    public static String trackHolesFile = "tracking.txt";

    /*-------------------- Profiling flags. ------------------------*/

    public static String profilingFile = "profiling.txt";
    public static boolean isProfiling = false;

    /*--------------------- Research flags. ------------------------*/

    // If we turn on any of the research flags, then we have to turn
    // off hot filling optimization so we can collect dynamic
    // information during invocation of our apis.

    /**
     * Count how many conditions always evaluated to true and false.
     */
    // isProfiling should be turned on as we currently output to the
    // same profiling file.
    public static boolean dynamicCollecting = false;

    /**
     * Determine if we save values evaluated from holes.
     */
    public static boolean saveHoleValues = false;
    public static String holeValuesFile = "values.txt";

    /**
     * Determine if we count how many exception of invalid array
     * index, including NegativeArraySizeException and
     * ArrayIndexOutOfBoundException.
     */
    public static boolean countInvalidArrIdxException = false;
    public static String invalidArrIdxExceptionFile = "count-exceptions.txt";

    /*--------------------- Testing flags. -------------------------*/

    /**
     * Determine if we also transform APIs in test template program
     * besides including a loop with checksum in main.
     */
    public static boolean transformApi = true;

    /**
     * Only used when {@link Config#transformApi} is {@code false}.
     */
    public static String outputWOTransformedClzName;

    /**
     * Determine if we allow generated programs to be written into the
     * disk.
     */
    public static boolean disableOutput = false;

    /**
     * If true, mimic execution of generated programs, which means
     * doing hash and checksum and print out, with full iterations.
     */
    public static boolean mimicExecution = false;
}
