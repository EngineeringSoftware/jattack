package jattack.util;

import jattack.Constants;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Utility class for types.
 */
public class TypeUtil {

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

    public static void setStaticFieldToDefaultValue(Field f) {
        try {
            setStaticFieldToDefaultValue0(f);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setStaticFieldToDefaultValue0(Field f)
            throws IllegalAccessException {
        Class<?> clz = f.getType();
        f.setAccessible(true);
        if (clz.equals(Byte.TYPE)) {
            f.setByte(null, (byte) 0);
        } else if (clz.equals(Short.TYPE)) {
            f.setShort(null, (short) 0);
        } else if (clz.equals(Integer.TYPE)) {
            f.setInt(null, 0);
        } else if (clz.equals(Long.TYPE)) {
            f.setLong(null, 0L);
        } else if (clz.equals(Float.TYPE)) {
            f.setFloat(null, 0.0f);
        } else if (clz.equals(Double.TYPE)) {
            f.setDouble(null, 0.0d);
        } else if (clz.equals(Character.TYPE)) {
            f.setChar(null, '\u0000');
        } else if (clz.equals(Boolean.TYPE)) {
            f.setBoolean(null, false);
        } else {
            f.set(null, null);
        }
    }

    /**
     * Get a list of names of all the nested classes of the given
     * class.
     */
    public static String[] getNestedClassNames(Class<?> clz) {
        return Arrays.stream(clz.getDeclaredClasses())
                .map(Class::getName).toArray(String[]::new);
    }

    /**
     * Deeply copy a multidimensional array. Assume the base type is
     * a primitive type.
     */
    private static Object deepCopyArray(Object arr) {
        Class<?> arrClz = arr.getClass();
        if (!arrClz.isArray()) {
            throw new IllegalArgumentException("Expect an array but got: " + arrClz);
        }

        int len = Array.getLength(arr);
        Class<?> compClz = arrClz.getComponentType();
        Object arrCpy = Array.newInstance(compClz, len);
        for (int i = 0; i < len; i++) {
            Object elem = Array.get(arr, i);
            Array.set(arrCpy, i, deepCopy(elem));
        }
        return arrCpy;
    }

    /**
     * Deeply copy an object.
     */
    private static Object deepCopy(Object obj) {
        if (obj == null) {
            return null;
        }
        Class<?> clz = obj.getClass();
        if (org.csutil.util.TypeUtil.isImmutable(clz)) {
            return obj;
        } else if (clz.isArray()) {
            return deepCopyArray(obj);
        } else {
            // TODO: reference types
            return obj;
        }
    }

    /**
     * NOTE this method has side effect, which updates status passed in.
     */
    public static boolean captureStatusAndEquals(
            Class<?> clz, Set<String> fieldNames,
            Map<String, Object> status)
            throws NoSuchFieldException, IllegalAccessException {
        boolean isSame = true;
        for (String fieldName : fieldNames) {
            Field field = clz.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object newVal = field.get(null);
            if (!status.containsKey(fieldName)
                    || !objectEquals(newVal, status.get(fieldName))) {
                isSame = false;
                status.put(fieldName, deepCopy(newVal));
            }
        }
        return isSame;
    }

    private static boolean objectEquals(Object v1, Object v2) {
        // at least primitives, strings and arrays are fine.
        // TODO: other types.
        return Objects.deepEquals(v1, v2);
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
                fieldValues.put(field.getName(), deepCopy(field.get(null)));
            }
        }
        return fieldValues;
    }

    /**
     * Get the values of the specified static fields of the given
     * class.
     */
    public static Map<String, Object> captureSpecifiedStatus(
            Class<?> clz, Set<String> fieldNames)
            throws NoSuchFieldException, IllegalAccessException {
        Map<String, Object> fieldValues = new HashMap<>();
        for (String fieldName : fieldNames) {
            Field field = clz.getDeclaredField(fieldName);
            field.setAccessible(true);
            fieldValues.put(fieldName, deepCopy(field.get(null)));
        }
        return fieldValues;
    }

    private static void deepSet(Object obj, Object val) {
        if (obj == null) {
            if (val != null) {
                throw new RuntimeException("Cannot set state for null!");
            }
            return;
        }
        Class<?> clz = obj.getClass();
        if (org.csutil.util.TypeUtil.isImmutable(clz)) {
            if (!Objects.equals(val, obj)) {
                throw new RuntimeException("Cannot set state for primitives or strings!");
            }
        } else if (clz.isArray()) {
            deepSetArray(obj, val);
        } else {
            // TODO: reference types
        }
    }

    private static void deepSetArray(Object arr, Object val) {
        Class<?> arrClz = arr.getClass();
        if (!arrClz.isArray()) {
            throw new IllegalArgumentException("Expect an array but got: " + arrClz);
        }

        int len = Array.getLength(arr);
        for (int i = 0; i < len; i++) {
            Object elem = Array.get(val, i);
            // TODO: check Array.get(arr, i) has the same type of elem
            Array.set(arr, i, deepCopy(elem));
        }
    }

    /**
     * Set static fields of the given class using the given values.
     */
    public static void recoverStatus(
            Class<?> clz,
            Map<String, Object> fieldValues)
            throws NoSuchFieldException, IllegalAccessException {
        for (Map.Entry<String, Object> e : fieldValues.entrySet()) {
            Field field = clz.getDeclaredField(e.getKey());
            field.setAccessible(true);
            Object val = e.getValue();
            if (isFinal(field)) {
                // For a final field, we cannot directly modify its
                // value so we set its state.
                Object obj = field.get(null);
                deepSet(obj, val);
            } else {
                // For a non-final field, we directly change its
                // value.
                field.set(null, deepCopy(val));
            }
        }
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

    public static Class<?> loadClz(
        String className,
        boolean initialize)
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

    /**
     * Returns true if the given descriptor supported.
     * @param desc the given descriptor
     * @return {@code true} if the given descriptor is supported;
     *         {@code false} otherwise.
     */
    public static boolean isTypeDescSupported(String desc) {
        return desc.startsWith("[")
                || desc.startsWith("L")
                || desc.equals("I")
                || desc.equals("Z")
                || desc.equals("D")
                || desc.equals("J");
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

    public static String primitiveDescToName(String desc) {
        return primitiveNameByDesc.get(desc);
    }

    private static final Map<String, String> wrappedByPrimitive = Map.of(
            "Z", "java/lang/Boolean",
            "B", "java/lang/Byte",
            "C", "java/lang/Character",
            "D", "java/lang/Double",
            "F", "java/lang/Float",
            "I", "java/lang/Integer",
            "J", "java/lang/Long",
            "S", "java/land/Short"
    );

    public static boolean isPrimitive(String typeDesc) {
        return wrappedByPrimitive.containsKey(typeDesc);
    }

    /**
     * Returns the corresponding wrapped class name of the descriptor
     * of the given primitive type.
     * @param desc the descriptor of the given primitive type
     * @return the corresponding wrapped class name
     */
    public static String primitiveDescToWrappedInternName(String desc) {
        return wrappedByPrimitive.get(desc);
    }

    /**
     * Returns the descriptor of corresponding wrapped class of the
     * descriptor of the given primitive type.
     * @param desc the descriptor of the given primitive type
     * @return the descriptor of corresponding wrapped class
     */
    public static String primitiveDescToWrappedDesc(String desc) {
        return intern2Desc(primitiveDescToWrappedInternName(desc));
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
        return internName.startsWith("[") ?
                internName :
                String.format("L%s;", internName);
    }

    public static String bin2Desc(String binClassName) {
        return intern2Desc(bin2Intern(binClassName));
    }

    public static String desc2Intern(String typeDesc) {
        if (isPrimitive(typeDesc)) {
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
     * is the given {@linkplain Class}.
     *
     * @return a {@code Class} describing the array type
     */
    public static Class<?> toArrayType(Class<?> type) {
        return Array.newInstance(type, 0).getClass();
    }
}
