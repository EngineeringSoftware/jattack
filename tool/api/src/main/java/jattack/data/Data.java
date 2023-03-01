package jattack.data;

import org.objectweb.asm.Type;
import jattack.Config;
import jattack.ast.Node;
import jattack.bytecode.Symbol;
import jattack.bytecode.Var;
import jattack.util.TypeUtil;
import jattack.util.UniqueList;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Class to store metadata used.
 */
// TODO: encapsulate with separate classes for many data structures in
//  this big class, e.g., {@link Data#memory}.
public class Data {

    /**
     * If the template has static initializer, i.e., <clinit>.
     */
    private static Set<String> hasStaticInitializer = new HashSet<>();

    public static boolean hasStaticInitializer(String className) {
        return hasStaticInitializer.contains(className);
    }

    public static void setHasStaticInitializer(String className) {
        hasStaticInitializer.add(className);
    }

     /**
     * Cache to store string representations of AST nodes generated
     * in the current run.
     * <p>
     * Maps hole identifiers to strings.
     * Reset per run.
     */
    private static Map<Integer, String> strCache = new HashMap<>();

    public static void resetStrCache() {
        strCache = new HashMap<>();
    }

    public static boolean isNoHoleFilled() {
        return strCache.isEmpty();
    }

    public static String getJavaStrOfHole(int holeId) {
        return strCache.get(holeId);
    }

    public static int getNumFilledHoles() {
        return strCache.size();
    }


    public static void saveToStrCache(int holeId, String nodeStr) {
        Data.strCache.put(holeId, nodeStr);
    }

    /**
     * Returns true this is the second or more time in the current run
     * we eval the hole with the given holeId.
     */
    public static boolean isTheHoleFilled(int holeId) {
        return strCache.containsKey(holeId);
    }

    /**
     * Collection of {@link Data#strCache} in all the previous runs,
     * which helps us decide if we get a duplicated program from the
     * current run.
     */
    private static UniqueList<Map<Integer, String>> pastStrCache = new UniqueList<>();

    /**
     * Returns true if this run gives a new program; return false if
     * this run outputs a duplicated program.
     */
    public static boolean saveToPastStrCache() {
        return Data.pastStrCache.add(strCache);
    }

    /**
     * Cache to store asts we constructed because we need to reuse
     * them over and over again after the first instantiation.
     * <p>
     * Maps hole identifiers to ASTs.
     */
    private static final Map<Integer, Node<?>> astCache = new HashMap<>();

    public static void clearASTCache() {
        astCache.clear();
    }

    public static Node<?> getASTOfHole(int holeId) {
        return astCache.get(holeId);
    }

    public static Node<?> addToASTCacheIfAbsent(int holeId, Node<?> ast) {
        return astCache.putIfAbsent(holeId, ast);
    }

    public static Map<Integer, Node<?>> getASTCache() {
        return astCache;
    }

    /**
     * All the hole identifiers.
     */
    private static Set<Integer> totalHoles;

    public static void setTotalHoles(Set<Integer> holes) {
        totalHoles = holes;
    }

    public static int getTotalNumHoles() {
        return totalHoles.size();
    }

    public static boolean thisTmplHasHole() {
        return !totalHoles.isEmpty();
    }

    /**
     * A list of all the holes explored in the current run in the
     * order of execution.
     * <p>
     * Reset per run.
     */
    public static UniqueList<Integer> holeVector = new UniqueList<>();
    public static int firstHoleIdxThatShouldStepInNextRun = -1;

    private static int findFirstHoleToStepInNextRun() {
        int i;
        for (i = holeVector.size() - 1; i >= 0; i--) {
            Node<?> ast = astCache.get(holeVector.get(i));
            if (ast.itr().hasNext()) {
                break;
            }
        }
        // If none of the holes has next, which means exhaustive, then
        // i will be -1; otherwise i will be [0, holeVector.size()).
        return i;
    }

    public static void setFirstHoleIdxThatShouldStepInNextRun() {
        firstHoleIdxThatShouldStepInNextRun = findFirstHoleToStepInNextRun();
    }

    /**
     * Set that contains the identifiers of all the holes that have
     * systematically explored all the possibilities.
     */
    public static final Set<Integer> holesThatHaveExploredAll = new HashSet<>();

    /**
     * Counters to track runs and outputs.
     */
    public static int runCount = 0;
    public static int outputCount = 0;

    /**
     * Returns true if all the reachable holes have been filled in the
     * current run.
     */
    public static boolean isAllReachableHolesFilled() {
        return strCache.size() + neverReachableHoles.size() == getTotalNumHoles();
    }

    /**
     * How many trials since the last time we have seen a new
     * generated program.
     */
    public static int repeatedTrials = 0;

    /**
     * Returns true if we are done.
     * <p>
     * For systematic ss, we are done when we have explored the whole
     * search space.
     * For random ss, we are done when we have enough outputs.
     * For smart ss, we are done when the hole with most choices has
     * explored all it has.
     */
    public static boolean isDone() {
        switch (Config.ss) {
        case SYSTEMATIC: {
            return firstHoleIdxThatShouldStepInNextRun == -1
                    || (!Config.isExhaustive && outputCount == Config.nOutputs);
        }
        case RANDOM: {
            return outputCount == Config.nOutputs
                    // We stop if we do not see any new generated
                    // program for a while, which is Config.maxRepeatedTrialsAllowed.
                    || repeatedTrials == Config.maxRepeatedTrialsAllowed;
        }
        case SMART: {
            // If each hole have explored all the possibilities it can,
            // then we are done with systematic exploration (in our
            // definition).
            // Assume astCache will be full from the first run,
            // this simple comparison is sufficient to decide.
            // TODO: this condition could never be satisfied in some
            //   cases due to unreachable holes.
            return holesThatHaveExploredAll.size() == getTotalNumHoles()
                    || (!Config.isExhaustive && outputCount == Config.nOutputs);
        }
        default:
            throw new RuntimeException("Unrecognized search strategy: "
                    + Config.ss + "!");
        }
    }

    /* ------------------------ Solver. ----------------------------*/

    /**
     * The holes that will be evaluated to true or false always (
     * given by solver) and the associated boolean values.
     * <p>
     * Reset per run.
     */
    private static Map<Integer, Boolean> alwaysTrueOrFalseCondHoles = new HashMap<>();

    public static void documentAlwaysTrueOrFalseHole(int holeId, boolean val) {
        alwaysTrueOrFalseCondHoles.put(holeId, val);
    }

    public static void resetAlwaysTrueOrFalseCondHoles() {
        alwaysTrueOrFalseCondHoles = new HashMap<>();
    }

    public static boolean isAlwaysTrueOrFlaseCondHole(int holeId) {
        return alwaysTrueOrFalseCondHoles.containsKey(holeId);
    }

    public static boolean getTrueOrFlase(int holeId) {
        return alwaysTrueOrFalseCondHoles.get(holeId);
    }

    // TODO: abstract a class for Hole.

    /**
     * The holes that are conditions of if statement or while/for
     * statements. Note we mean a complete condition not a clause of
     * it.
     */
    private static final Set<Integer> holesAsConditions = new HashSet<>();

    public static void addToHolesAsConditions(int holeId) {
        holesAsConditions.add(holeId);
    }

    public static boolean isTheHoleACondition(int holeId) {
        return holesAsConditions.contains(holeId);
    }

    /**
     * Holes that will be never reachable, where the condition is
     * unsatisfiable. Note this is different from saying a hole is
     * non-reachable, which means the hole will not be reached under
     * current context or iterations.
     * <p>
     * Reset per run.
     */
    private static Set<Integer> neverReachableHoles;

    public static void resetNeverReachableHoles() {
        neverReachableHoles = new HashSet<>();
    }

    public static void addToNeverReachableHoles(int holeId) {
        neverReachableHoles.add(holeId);
    }

    public static boolean isTheHoleNeverReachable(int holeId) {
        return neverReachableHoles.contains(holeId);
    }

    /*------------------------- Research. --------------------------*/

    /**
     * Store the values of conditions in previous evaluation.
     * <p>
     * Reset per run.
     */
    private static Map<Integer, Boolean> condPrevVals;

    /**
     * The set of conditions evaluated to different values at
     * different iterations in a single run.
     * <p>
     * Reset per run.
     */
    private static Set<Integer> condsThatChangedVals;

    public static void collectCondVals(int holeId, boolean val) {
        // See if the condition is evaluated to a new value
        if (condsThatChangedVals.contains(holeId)) {
            // We don't care this condition anymore since we know it
            // has changed its value
            return;
        }
        if (condPrevVals.containsKey(holeId)) {
            if (val != condPrevVals.get(holeId)) {
                condsThatChangedVals.add(holeId);
            }
        } else {
            condPrevVals.put(holeId, val);
        }
    }

    public static void resetCondValsCollection() {
        condPrevVals = new HashMap<>();
        condsThatChangedVals = new HashSet<>();
    }

    public static int getNumAllCondsEvaled() {
        return condPrevVals.size();
    }

    /**
     * Return the number of conditions that were always evaluated to
     * true (the first entry of the returned array) and false (the
     * second entry of the returned array).
     */
    public static int[] getNumConstConds() {
        int[] count = new int[] {0, 0};
        for (Map.Entry<Integer, Boolean> cond : condPrevVals.entrySet()) {
            if (!condsThatChangedVals.contains(cond.getKey())) {
                count[cond.getValue() ? 0 : 1] += 1;
            }
        }
        return count;
    }


    /**
     * The values evaluated of every condition hole.
     * <p>
     * Reset per run.
     */
    private static Map<Integer, List<Boolean>> condVals;

    public static Map<Integer, List<Boolean>> getCondVals() {
        return condVals;
    }

    public static void resetCondVals() {
        condVals = new HashMap<>();
    }

    public static void saveCondVals(int holeId, boolean val) {
        // Save the value evaluated
        condVals.putIfAbsent(holeId, new LinkedList<>());
        condVals.get(holeId).add(val);
    }

    /*------------------------ Infer variables ---------------------*/

    /**
     * Local variables for every method.
     * <p>
     * Reset per bytecode manipulation in in-memory compiler.
     */
    private static Map<String, UniqueList<Var>> localVars;

    public static void addToLocalVars(String fullMethodName, Set<Var> variables) {
        localVars.put(fullMethodName, new UniqueList<>(variables));
    }

    public static void resetLocalVars() {
        localVars = new HashMap<>();
    }

    public static UniqueList<Var> getLocalVarsOfMethod(String fullMethodName) {
        return localVars.get(fullMethodName);
    }

    /**
     * Offsets of all eval() in the order of occurrence for every
     * method.
     */
    private static Map<String, Deque<Integer>> offsetsOfEvals;

    public static Deque<Integer> getOffsetsOfEvalsOfMethod(String fullMethodName) {
        return offsetsOfEvals.get(fullMethodName);
    }

    public static void addToOffsetsOfEvals(String fullMethodName, int offset) {
        offsetsOfEvals.putIfAbsent(fullMethodName, new ArrayDeque<>());
        offsetsOfEvals.get(fullMethodName).offer(offset);
    }
    public static void resetOffsetsOfEvals() {
        offsetsOfEvals = new HashMap<>();
    }

    /**
     * Map from the name of a symbol (local variable or field) to its
     * value.
     * <p>
     * Reset before per eval() through instrumentation.
     */
    private static Memory memory;

    public static Map<String, Object> getMemory() {
        return memory.getTable();
    }

    public static boolean memoryContainsVar(String name) {
        return memory.containsKey(name);
    }

    public static void addToMemory(String name, Object val) {
        memory.put(name, val);
    }

    // Used through instrumentation, don't believe your IDE!
    public static void resetMemory() {
        if (memory == null) {
            memory = new Memory();
        } else {
            memory.reset();
        }
    }

    /**
     * Gets the runtime value of the given variable. Returning
     * {@code null} means either the value is {@code null} or the
     * given variable does not exist in memory.
     * TODO: wrap null in a special class
     */
    public static Object getFromMemoryValueOfVar(String name) {
        return memory.get(name);
    }

    public static Set<String> getVarsOfType(Class<?> type) {
        if (Config.staticGen) {
            return staticGetVarsOfType(type);
        } else {
            return getVarsOfTypeFromMemory(type);
        }
    }

    private static Set<String> getVarsOfTypeFromMemory(Class<?> type) {
        TreeSet<String> ids = new TreeSet<>(String::compareTo);
        for (Map.Entry<String, Object> e : memory.getTable().entrySet()) {
            Object val = e.getValue();
            if (TypeUtil.isBoxed(type) && val == null) {
                // null cannot be assigned to any primitive type
                continue;
            }
            // null can be assigned to any referencey type
            if (val == null || type.isAssignableFrom(val.getClass())) {
                ids.add(e.getKey());
            }
        }
        return ids;
    }

    /*---------------------- Static generation ---------------------*/

    /**
     * hole id -> available variables (fields and local variables)
     */
    private static Map<Integer, Set<Symbol>> varsByHole = new HashMap<>();

    public static Map<Integer, Set<Symbol>> getVarsByHole() {
        return varsByHole;
    }

    public static void addToVarsByHole(int hole) {
        varsByHole.put(hole, new HashSet<>());
    }

    public static void addToVarsByHole(int hole, Symbol var) {
        varsByHole.get(hole).add(var);
    }

    public static void resetVarsByHole() {
        varsByHole = new HashMap<>();
    }

    /**
     * method -> holes in this method
     */
    private static Map<String, Set<Integer>> holesByMethod = new HashMap<>();

    public static Map<String, Set<Integer>> getHolesByMethod() {
        return holesByMethod;
    }

    public static Set<Integer> getHolesOfMethod(String method) {
        return holesByMethod.get(method);
    }

    public static boolean hasHoleInMethod(String method) {
        return holesByMethod.containsKey(method);
    }

    public static void addToHolesByMethod(String method, int holeId) {
        holesByMethod.putIfAbsent(method, new HashSet<>());
        holesByMethod.get(method).add(holeId);
    }

    /**
     * The current hole we are statically filling.
     */
    private static int currHoleId;

    public static void setCurrHoleId(int holeId) {
        currHoleId = holeId;
    }

    public static Set<String> staticGetVarsOfType(Class<?> type) {
        return staticGetVarsOfType(type, currHoleId);
    }
    public static Set<String> staticGetVarsOfType(Class<?> type, int hole) {
        Set<Symbol> localVars = varsByHole.get(hole);
        TreeSet<String> ids = new TreeSet<>(String::compareTo);
        for (Symbol v : localVars) {
            try {
                Class<?> vType = Class.forName(TypeUtil.desc2Bin(v.getDesc()));
                if (type.isAssignableFrom(vType)) {
                    ids.add(v.getName());
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return ids;
    }
}
