package jattack.data;

import jattack.Config;
import jattack.ast.Node;
import jattack.bytecode.Symbol;
import jattack.bytecode.Var;
import jattack.util.TypeUtil;
import jattack.util.UniqueList;

import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Class to store metadata used.
 */
// TODO: encapsulate with separate classes for many data structures in
//  this big class, e.g., {@link Data#memory}.
public class Data {

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
     * Offsets of all eval() in the order of occurrence for every
     * method.
     * <p>
     * Reset per bytecode manipulation in in-memory compiler.
     */
    private static Map<String, Deque<Integer>> offsetsOfEvals;

    public static Deque<Integer> getOffsetsOfEvalsOfMethod(String fullMethodName) {
        return offsetsOfEvals.get(fullMethodName);
    }

    public static void addToOffsetsOfEvals(
            String fullMethodName, Deque<Integer> offsets) {
        offsetsOfEvals.put(fullMethodName, offsets);
    }

    public static void resetOffsetsOfEvals() {
        offsetsOfEvals = new HashMap<>();
    }

    /**
     * The set of local variables that are accessible in every eval()
     * for every method.
     * <p>
     * Reset per bytecode manipulation in in-memory compiler.
     */
    private static Map<String, Map<Integer, Set<Var>>> accessibleLocalVars;

    public static Set<Var> getAccessibleLocalVars(String fullMethodName, int offset) {
        return accessibleLocalVars.get(fullMethodName).get(offset);
    }

    public static void addToAccessibleVarsInEachEval(
            String fullMethodName,
            Set<Var> localVarsOfTheMethod,
            Deque<Integer> offsetsOfEvalsInTheMethod) {
        if (!offsetsOfEvals.containsKey(fullMethodName)) {
            // no eval() in this method
            return;
        }
        Map<Integer, Set<Var>> m = new HashMap<>();
        for (int offset : offsetsOfEvalsInTheMethod) {
            m.put(offset, getAccessibleVarsForSingleEval(offset, localVarsOfTheMethod));
        }
        accessibleLocalVars.put(fullMethodName, m);
    }

    private static Set<Var> getAccessibleVarsForSingleEval(
            int offset, Set<Var> localVars) {
        return localVars.stream()
                    .filter(var -> var.isReachableAt(offset))
                    .collect(Collectors.toSet());
    }

    public static void resetAccessibleLocalVars() {
        accessibleLocalVars = new HashMap<>();
    }

    /**
     * Map from the name of a symbol (local variable or field) to its
     * value.
     * <p>
     * Reset before per eval() through instrumentation.
     */
    private static Memory memory;

    public static Map<String, RuntimeSymbol> getMemory() {
        return memory.getTable();
    }

    public static boolean memoryContainsSymbol(String name) {
        return memory.containsKey(name);
    }

    public static void addToMemory(String name, String desc, Object val) {
        memory.put(name, desc, val);
    }

    /* The following list of addToMemory is used only in
     * instrumentation. */

    public static void addToMemory(String name, String desc, boolean val) {
        addToMemory(name, desc, (Object) val);
    }

    public static void addToMemory(String name, String desc, byte val) {
        addToMemory(name, desc, (Object) val);
    }

    public static void addToMemory(String name, String desc, short val) {
        addToMemory(name, desc, (Object) val);
    }

    public static void addToMemory(String name, String desc, char val) {
        addToMemory(name, desc, (Object) val);
    }

    public static void addToMemory(String name, String desc, int val) {
        addToMemory(name, desc, (Object) val);
    }

    public static void addToMemory(String name, String desc, float val) {
        addToMemory(name, desc, (Object) val);
    }

    public static void addToMemory(String name, String desc, long val) {
        addToMemory(name, desc, (Object) val);
    }

    public static void addToMemory(String name, String desc, double val) {
        addToMemory(name, desc, (Object) val);
    }

    public static void updateMemory(String name, Object val) {
        memory.updateValue(name, val);
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
     * Get the decalring type of the given symbol (field or local
     * variable).
     */
    public static Class<?> getDeclaringTypeOfSymbol(String name) {
        return desc2Clz(memory.getTable().get(name).getDesc());
    }

    /**
     * Gets the runtime value of the given symbol (field or local
     * variable). Returning {@code null} means the value is
     * {@code null}. If the symbol is not found in memory, an
     * {@link java.util.NoSuchElementException} will be thrown.
     */
    public static Object getFromMemoryValueOfSymbol(String name) {
        return memory.getValue(name);
    }

    /* The following list of getFromMemoryValueOfSymbol* is used only
     * in instrumentation and guaranteed to be type safe. */

    public static boolean getFromMemoryValueOfSymbolZ(String name) {
        return (boolean) getFromMemoryValueOfSymbol(name);
    }

    public static byte getFromMemoryValueOfSymbolB(String name) {
        return (byte) getFromMemoryValueOfSymbol(name);
    }

    public static short getFromMemoryValueOfSymbolS(String name) {
        return (short) getFromMemoryValueOfSymbol(name);
    }

    public static char getFromMemoryValueOfSymbolC(String name) {
        return (char) getFromMemoryValueOfSymbol(name);
    }

    public static int getFromMemoryValueOfSymbolI(String name) {
        return (int) getFromMemoryValueOfSymbol(name);
    }

    public static float getFromMemoryValueOfSymbolF(String name) {
        return (float) getFromMemoryValueOfSymbol(name);
    }

    public static long getFromMemoryValueOfSymbolJ(String name) {
        return (long) getFromMemoryValueOfSymbol(name);
    }

    public static double getFromMemoryValueOfSymbolD(String name) {
        return (double) getFromMemoryValueOfSymbol(name);
    }

    public static Set<String> getSymbolsOfType(Class<?> type) {
        if (Config.staticGen) {
            return staticGetSymbolsOfType(type);
        } else {
            return getSymbolsOfTypeFromMemory(type);
        }
    }

    private static Set<String> getSymbolsOfTypeFromMemory(Class<?> assignedType) {
        TreeSet<String> ids = new TreeSet<>(String::compareTo);
        for (RuntimeSymbol symbol : memory.getTable().values()) {
            String name = symbol.getName();
            String sDesc = symbol.getDesc();
            // We use declaring type rather than runtime type because
            // hole filling is on source code level
            Class<?> sDeclType = desc2Clz(sDesc);
            if (assignedType.isAssignableFrom(sDeclType)) {
                ids.add(name);
            }
        }
        return ids;
    }

    private static Class<?> desc2Clz(String desc) {
        try {
            return TypeUtil.loadClz(TypeUtil.desc2Bin(desc));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /*----- Data Collected from first loading template classes -----*/

    private static boolean recordedClassesInitOrder = false;

    public static boolean hasRecordedClassInitOrder() {
        return recordedClassesInitOrder;
    }

    public static void finishRecordingClassInitOrder() {
        recordedClassesInitOrder = true;
    }

    /**
     * The list records the order of class initialization.
     * Note, the class without static initializer is not included in
     * the list.
     */
    private static final UniqueList<String> classesInitOrder = new UniqueList<>();

    public static void addClassInit(String className) {
        classesInitOrder.add(className);
    }

    public static boolean hasStaticInitializer(String className) {
        return classesInitOrder.contains(className);
    }

    public static UniqueList<String> getClassInitOrder() {
        return classesInitOrder;
    }

    /*------------- Exception thrown from template ---------------- */

    private static Throwable invocationTemplateException = null;

    public static boolean isInvocationTemplateException(Throwable e) {
        return invocationTemplateException == e;
    }

    public static void saveInvocationTemplateException(Throwable e) {
        invocationTemplateException = e;
    }

    public static void resetInvocationTemplateException() {
        invocationTemplateException = null;
    }

    /*---------------------- Static generation ---------------------*/

    /**
     * hole id -> available symbols (fields and local variables)
     */
    private static Map<Integer, Set<Symbol>> symbolsByHole = new HashMap<>();

    public static Map<Integer, Set<Symbol>> getSymbolsByHole() {
        return symbolsByHole;
    }

    public static void addToSymbolsByHole(int hole, Symbol symbol) {
        symbolsByHole.putIfAbsent(hole, new HashSet<>());
        symbolsByHole.get(hole).add(symbol);
    }

    public static void resetSymbolsByHole() {
        symbolsByHole = new HashMap<>();
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

    private static Set<String> staticGetSymbolsOfType(Class<?> type) {
        return staticGetSymbolsOfType(type, currHoleId);
    }

    private static Set<String> staticGetSymbolsOfType(Class<?> type, int hole) {
        Set<Symbol> avilableSymbols = symbolsByHole.get(hole);
        TreeSet<String> ids = new TreeSet<>(String::compareTo);
        for (Symbol s : avilableSymbols) {
            Class<?> sType = desc2Clz(s.getDesc());
            if (type.isAssignableFrom(sType)) {
                ids.add(s.getName());
            }
        }
        return ids;
    }
}
