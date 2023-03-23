package jattack.util;

import jattack.Constants;
import jattack.data.Data;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.objectweb.asm.Type;

/**
 * Utility class for types.
 */
public class TypeUtil {

    /**
     * Wrap {@link Array#get(Object, int, Object)}, saving exceptions
     * thrown before rethrowing them.
     */
    public static Object arrayGet(Object array, int index) {
        try {
            if (array == null) {
                RuntimeException e = new NullPointerException("the specified array is null");
                Data.saveInvocationTemplateException(e);
                throw e;
            }
            return Array.get(array, index);
        } catch (ArrayIndexOutOfBoundsException e) {
            Data.saveInvocationTemplateException(e);
            throw e;
        }
    }

    /**
     * Wrap {@link Array#set(Object, int, Object)}, saving exceptions
     * thrown before rethrowing them.
     */
    public static void arraySet(Object array, int index, Object value) {
        try {
            if (array == null) {
                RuntimeException e = new NullPointerException("the specified array is null");
                Data.saveInvocationTemplateException(e);
                throw e;
            }
            Array.set(array, index, value);
        } catch (ArrayIndexOutOfBoundsException e) {
            Data.saveInvocationTemplateException(e);
            throw e;
        }
    }

    public static int compare(Number n1, Number n2) {
        if (n1.getClass() != n2.getClass()) {
            throw new IllegalArgumentException(
                    "Cannot compare two different types: " +
                            n1.getClass() + " and " + n2.getClass());
        }
        Class<?> type = n1.getClass();
        // TODO: there should be some approach to simplify the
        //  comparison
        if (Integer.class.equals(type)) {
            return Integer.compare((Integer) n1, (Integer) n2);
        } else if (Long.class.equals(type)) {
            return Long.compare((Long) n1, (Long) n2);
        } else if (Float.class.equals(type)) {
            return Float.compare((Float) n1, (Float) n2);
        } else if (Double.class.equals(type)) {
            return Double.compare((Double) n1, (Double) n2);
        } else if (Byte.class.equals(type)) {
            return Double.compare((Byte) n1, (Byte) n2);
        } else if (Short.class.equals(type)) {
            return Short.compare((Short) n1, (Short) n2);
        } else {
            throw new RuntimeException(
                    "Unexpected type for comparison: " + type);
        }
    }

    /**
     * Get the values of all the immutable (primitives or strings)
     * static fields of the given class.
     */
    public static Map<String, Object> captureImmutableStaticFieldValues(Class<?> clz)
            throws IllegalAccessException {
        Map<String, Object> fieldValues = new HashMap<>();
        for (Field field : clz.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (!Modifier.isStatic(modifiers)) {
                continue;
            }
            field.setAccessible(true);
            if (org.csutil.util.TypeUtil.isImmutable(field.getType())) {
                fieldValues.put(field.getName(), field.get(null));
            }
        }
        return fieldValues;
    }

    /**
     * Get the values of all the static fields of mutable reference
     * type for the given class.
     */
    public static Set<String> getMutableStaticFieldNames(Class<?> clz) {
        Set<String> fieldNames = new HashSet<>();
        for (Field field : clz.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (!Modifier.isStatic(modifiers)) {
                continue;
            }
            if (!org.csutil.util.TypeUtil.isImmutable(field.getType())) {
                fieldNames.add(field.getName());
            }
        }
        return fieldNames;
    }

    /**
     * Parse out the simple name given a full qualified class name.
     * <p>
     * Note the current implementation does not support parsing names
     * for nested classes.
     */
    public static String getSimpleName(String binClzName) {
        return binClzName.substring(binClzName.lastIndexOf(".") + 1);
    }

    /**
     * Returns the fully qualified name of the package that the class
     * is a member of, or the empty string if the class is in an
     * unnamed package.
     * @param binClassName the given full qualified binary class name
     * @return the fully qualified package name
     */
    public static String getPackageName(String binClassName) {
        int dot = binClassName.lastIndexOf('.');
        return  (dot != -1) ? binClassName.substring(0, dot).intern() : "";
    }

    public static Class<?> loadClz(String className)
            throws ClassNotFoundException {
        return loadClz(className, true);
    }

    public static Class<?> loadClz(String className, boolean initialize)
            throws ClassNotFoundException {
        return loadClz(className, initialize, TypeUtil.class.getClassLoader());
    }

    public static Class<?> loadClz(
            String className,
            boolean initialize,
            ClassLoader cl)
            throws ClassNotFoundException {
        return Class.forName(className, initialize, cl);
    }

    public static Set<Class<?>> loadClzes(Collection<String> classNames)
            throws ClassNotFoundException {
    return loadClzes(classNames, true);
    }

    public static Set<Class<?>> loadClzes(
            Collection<String> classNames,
            boolean initialize)
            throws ClassNotFoundException {
        return loadClzes(classNames, initialize, TypeUtil.class.getClassLoader());
    }

    public static Set<Class<?>> loadClzes(
            Collection<String> classNames,
            boolean initialize,
            ClassLoader cl)
            throws ClassNotFoundException {
        Set<Class<?>> clzes = new HashSet<>();
        for (String className : classNames) {
            clzes.add(loadClz(className, initialize, cl));
        }
        return clzes;
    }

    /**
     * Build a full method name by concatenating binary class name,
     * method name and method descriptor.
     */
    public static String toFullMethodName(
            String className,
            String methodName,
            String methodDesc) {
        return className + ":" + methodName + methodDesc;
    }

    private static final String[] javaOwnPkgs = {
            "java.",
            "javax.",
            "jdk.",
            "sun.",
            "com.sun."
    };

    public static boolean isJavaOwnClass(String className) {
        for (String pkg : javaOwnPkgs) {
            if (className.startsWith(pkg)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isToolOwnClass(String className) {
        if (className.startsWith(Constants.EXAMPLE_PKG)) {
            // Exclude example directory as it is for testing
            return false;
        }
        return className.startsWith(Constants.ROOT_PKG + ".");
    }

    public static boolean isTypeSupported(Field field) {
        String desc = Type.getDescriptor(field.getType());
        return isTypeDescSupported(desc);
    }

    /**
     * Returns true if the given descriptor supported.
     * @param desc the given descriptor
     * @return {@code true} if the given descriptor is supported;
     *         {@code false} otherwise.
     */
    public static boolean isTypeDescSupported(String desc) {
        return desc.startsWith("[")
                || desc.startsWith("L")
                || desc.equals("Z")
                || desc.equals("B")
                || desc.equals("C")
                || desc.equals("D")
                || desc.equals("F")
                || desc.equals("I")
                || desc.equals("J")
                || desc.equals("S");
    }

    private static final Map<String, String> primitiveNameByDesc = Map.of(
            "Z", "boolean",
            "B", "byte",
            "C", "char",
            "D", "double",
            "F", "float",
            "I", "int",
            "J", "long",
            "S", "short"
    );

    private static final Map<String, String> descByPrimitiveName =
            primitiveNameByDesc.entrySet().stream().collect(
                    Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

    public static String primitiveDescToName(String desc) {
        return primitiveNameByDesc.get(desc);
    }

    public static String primitiveNameToDesc(String primitiveName) {
        return descByPrimitiveName.get(primitiveName);
    }

    public static boolean isNamePrimitive(String name) {
        return descByPrimitiveName.containsKey(name);
    }

    private static final Map<String, String> boxedByPrimitive = Map.of(
            "Z", "java/lang/Boolean",
            "B", "java/lang/Byte",
            "C", "java/lang/Character",
            "D", "java/lang/Double",
            "F", "java/lang/Float",
            "I", "java/lang/Integer",
            "J", "java/lang/Long",
            "S", "java/lang/Short"
    );

    public static boolean isDescPrimitive(String typeDesc) {
        return boxedByPrimitive.containsKey(typeDesc);
    }

    /**
     * Returns the internal name of corresponding boxed type of the
     * given primitive type.
     * @param desc the descriptor of the given primitive type
     * @return the internal name of corresponding boxed type
     */
    public static String primitiveDescToBoxedInternName(String desc) {
        return boxedByPrimitive.get(desc);
    }

    /**
     * Returns the descriptor of corresponding boxed type of the given
     * primitive type.
     * @param desc the descriptor of the given primitive type
     * @return the descriptor of corresponding boxed type
     */
    public static String primitiveDescToBoxedDesc(String desc) {
        return intern2Desc(primitiveDescToBoxedInternName(desc));
    }

    public static boolean isBoxed(Class<?> clz) {
        return org.csutil.util.TypeUtil.isBoxedPrimitive(clz);
    }

    public static Class<?> unbox(Class<?> clz) {
        return org.csutil.util.TypeUtil.boxedToPrimitive(clz);
    }

    /**
     * Returns true if the given {@code clz} is one of {@link Byte},
     * {@link Short}, {@link Integer}, {@link Long}, {@link Float},
     * {@link Double}; otherwise false.
     * @param obj the class to query
     * @return true if th given {@code clz} is one of {@link Byte},
     * {@link Short}, {@link Integer}, {@link Long}, {@link Float},
     * {@link Double}; otherwise false.
     */
    public static boolean isNumberBoxed(Class<?> clz) {
        return Number.class.isAssignableFrom(clz) && isBoxed(clz);
    }

    /**
     * Convert any fine-grained type descriptor to primitive types or
     * Object.
     */
    private static final Map<String, String> baseTypes = Map.of(
            "Z", "Z",
            "B", "B",
            "C", "C",
            "D", "D",
            "F", "F",
            "I", "I",
            "J", "J",
            "S", "S",
            "[", Constants.OBJECT_TYPE_DESC,
            "L", Constants.OBJECT_TYPE_DESC
    );

    public static String toBaseTypeDesc(String typeDesc) {
        return baseTypes.get(typeDesc.substring(0, 1));
    }

    public static String intern2Bin(final String internClassName) {
        return internClassName.replace('/', '.');
    }

    public static String bin2Intern(final String binClassName) {
        return binClassName.replace('.', '/');
    }

    public static String intern2Desc(String internName) {
        if (isNamePrimitive(internName)) {
            return primitiveNameToDesc(internName);
        }
        return internName.startsWith("[") ?
                internName :
                String.format("L%s;", internName);
    }

    public static String bin2Desc(String binClassName) {
        return intern2Desc(bin2Intern(binClassName));
    }

    public static String desc2Bin(String typeDesc) {
        return intern2Bin(desc2Intern(typeDesc));
    }

    public static String desc2Intern(String typeDesc) {
        if (isDescPrimitive(typeDesc)) {
            return primitiveDescToName(typeDesc);
        } else if (typeDesc.startsWith("[")) {
            return typeDesc;
        } else if (typeDesc.startsWith("L")) {
            // strip "L" and ";"
            return typeDesc.substring(1, typeDesc.length() - 1);
        } else {
            throw new RuntimeException("Unexpected type: " + typeDesc);
        }
    }

    public static int getArgSize(String typeDesc) {
        if (typeDesc.equals("J") || typeDesc.equals("D")) {
            return  2;
        } else {
            return  1;
        }
    }

    /**
     * Returns true if the given class is static; false otherwise.
     */
    public static boolean isStatic(Field field) {
        return isStatic(field.getModifiers());
    }

    /**
     * Returns true if the given method is static; false otherwise.
     */
    public static boolean isStatic(Method method) {
        return isStatic(method.getModifiers());
    }

    /**
     * Returns true if the given class is static; false otherwise.
     */
    public static boolean isStatic(Class<?> clz) {
        return isStatic(clz.getModifiers());
    }

    /**
     * Returns true if the given modifiers contains "static"; false
     * otherwise.
     * <p>
     * {@code modifiers} is obtained by {@code getModifiers()} via
     * reflection.
     */
    public static boolean isStatic(int modifiers) {
        return Modifier.isStatic(modifiers);
    }

    /**
     * Returns true if the given class is static; false otherwise.
     */
    public static boolean isFinal(Field field) {
        return isFinal(field.getModifiers());
    }

    /**
     * Returns true if the given modifiers contains "final"; false
     * otherwise.
     * <p>
     * {@code modifiers} is obtained by {@code getModifiers()} via
     * reflection.
     */
    public static boolean isFinal(int modifiers) {
        return Modifier.isFinal(modifiers);
    }

    /**
     * Returns a {@code Class} for an array type whose component type
     * is the given {@link Class}.
     *
     * @return a {@code Class} describing the array type
     */
    public static Class<?> toArrayType(Class<?> type) {
        return Array.newInstance(type, 0).getClass();
    }
}
