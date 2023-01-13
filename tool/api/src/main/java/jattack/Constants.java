package jattack;

import org.objectweb.asm.Opcodes;
import jattack.ast.Node;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Constants used in transformer.
 */
public class Constants {

    /*---------------------------- Packages. -----------------------*/

    public static final String TOOL_NAME = "jattack";
    public static final String ROOT_PKG = TOOL_NAME;
    public static final String DRIVER_PKG = ROOT_PKG + ".driver";
    public static final String DATA_PKG = ROOT_PKG + ".data";
    public static final String DATA_PKG_INTERN_NAME = ROOT_PKG + "/data";
    public static final String BYTECODE_PKG_INTERN_NAME = ROOT_PKG + "/bytecode";
    public static final String AST_PKG_INTERN_NAME = ROOT_PKG + "/api";
    public static final String EXP_PKG_INTERN_NAME = AST_PKG_INTERN_NAME + "/exp";
    public static final String EXAMPLE_PKG = ROOT_PKG + ".examples";
    public static final String ANNOT_PKG = ROOT_PKG + ".annotation";
    public static final String EXCEPT_PKG = ROOT_PKG + ".exception";
    public static final String UTIL_PKG = ROOT_PKG + ".util";

    public static final String CSUTIL_PKG = "org.csutil";

    /*---------------------------- Classes. ------------------------*/

    public static final String CONFIG_CLZ = ROOT_PKG + ".Config";
    public static final String DRIVER_CLZ = DRIVER_PKG + ".Driver";
    public static final String SEARCH_STRATEGY_CLZ = DRIVER_PKG + ".SearchStrategy";
    public static final String DATA_CLZ = DATA_PKG + ".Data";
    public static final String BOOM_CLZ = ROOT_PKG + ".Boom";
    public static final String DATA_CLZ_INTERN_NAME = DATA_PKG_INTERN_NAME + "/Data";
    public static final String VAR_CLZ_INTERN_NAME = BYTECODE_PKG_INTERN_NAME + "/Var";
    public static final String FIELD_ANALYZER_CLZ_INTERN_NAME = BYTECODE_PKG_INTERN_NAME + "/FieldAnalyzer";
    public static final String EXP_CLZ_INTERN_NAME = EXP_PKG_INTERN_NAME + "/Exp";
    public static final String INVOKED_FROM_NOT_DRIVER_EXCEPTION_CLZ = EXCEPT_PKG + ".InvokedFromNotDriverException";
    public static final String IOUTIL_CLZ = UTIL_PKG + ".IOUtil";

    public static final String WRAPPED_CHECKSUM_CLZ = CSUTIL_PKG + ".checksum.WrappedChecksum";
    public static final String HELPER_CLZ = CSUTIL_PKG + ".Helper";

    public static final String OBJECT_CLZ_INTERN_NAME = "java/lang/Object";
    public static final String MAP_CLZ_INTERN_NAME = "java/util/Map";
    public static final String ITERATOR_CLZ_INTERN_NAME = "java/util/Iterator";

    /*---------------------------- Types. --------------------------*/

    public static final String OBJECT_TYPE_DESC = "Ljava/lang/Object;";
    public static final String CLASS_TYPE_DESC = "Ljava/lang/Class;";
    public static final String STRING_TYPE_DESC = "Ljava/lang/String;";
    public static final String LIST_TYPE_DESC = "Ljava/util/List;";
    public static final String MAP_TYPE_DESC = "Ljava/util/Map;";
    public static final String ITERATOR_TYPE_DESC = "Ljava/util/Iterator;";

    /*---------------------------- Methods. ------------------------*/

    public static final String EVAL_METH_NAME = "eval";
    public static final String EVAL_METH_DESC =
            String.format("(I)%s", OBJECT_TYPE_DESC);
    public static final String INIT_FIELD_ANALYZER_METH_NAME = "initFieldAnalyzer";
    public static final String INIT_FIELD_ANALYZER_METH_DESC = "()V";
    public static final String FIND_FIELDS_METH_NAME = "findFields";
    public static final String FIND_FIELDS_METH_DESC1 =
            String.format("(%s)V", OBJECT_TYPE_DESC);
    public static final String FIND_FIELDS_METH_DESC2 =
            String.format("(%s)V", CLASS_TYPE_DESC);
    public static final String SAVE_FIELD_VALUES_METH_NAME = "saveFieldValues";
    public static final String SAVE_FIELD_VALUES_METH_DESC= "()V";
    public static final String UPDATE_FIELD_VALUES_METH_NAME = "updateFieldValues";
    public static final String UPDATE_FIELD_VALUES_METH_DESC = "()V";
    public static final String RESET_MEMORY_METH_NAME = "resetMemory";
    public static final String RESET_MEMORY_METH_DESC = "()V";
    public static final String ADD_TO_MEMORY_METH_NAME = "addToMemory";
    public static final String ADD_TO_MEMORY_METH_DESC =
            String.format("(%s%s)V", STRING_TYPE_DESC, OBJECT_TYPE_DESC);
    public static final String GET_FROM_MEMORY_VALUE_OF_VAR_METH_NAME =
            "getFromMemoryValueOfVar";
    public static final String GET_FROM_MEMORY_VALUE_OF_VAR_METH_DESC =
            String.format("(%s)%s", STRING_TYPE_DESC, OBJECT_TYPE_DESC);
    public static final Set<String> API_NAMES =
            Arrays.stream(Boom.class.getDeclaredMethods())
                .filter(m -> Node.class.isAssignableFrom(m.getReturnType()))
                .map(Method::getName)
                .collect(Collectors.toSet());
    public static final String GET_AST_CACHE_METHOD = "getASTCache";
    public static final String TRACK_METHOD = "$_" + TOOL_NAME + "_track";
    public static final String WRITE_TRACKING_RESULTS_METHOD =
            "$_" + TOOL_NAME + "_writeTrackingResults";
    public static final String STATIC_INITIALIZER_COPY_METHOD = "clinit$copy";

    /*--------------------------- Variables. -----------------------*/

    public static final String IS_DIRVEN_VAR = DRIVER_CLZ + ".isDriven";
    public static final String SS_VAR = CONFIG_CLZ + ".ss";
    public static final String FILLED_HOLES = "$_" + TOOL_NAME + "_filledHoles";
    public static final String TOTAL_HOLES = "$_" + TOOL_NAME + "_totalHoles";

    /*------------------------- Annotations. -----------------------*/

    public static final String ENTRY_ANNOT = "Entry";
    public static final String ENTRY_ANNOT_FULL_NAME = ANNOT_PKG + ".Entry";
    public static final String ARGUMENT_ANNOT = "Argument";
    public static final String ARGUMENT_ANNOT_FULL_NAME = ANNOT_PKG + ".Argument";

    /*--------------------------- Others. --------------------------*/

    public static final String SS_SYSTEMATIC = SEARCH_STRATEGY_CLZ + ".SYSTEMATIC";

    /*---------------------------- Misc. ---------------------------*/

    public static final int ASM_VERSION = Opcodes.ASM9;

    /*---------------------------- Util. ---------------------------*/

}
