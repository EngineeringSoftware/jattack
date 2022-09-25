package org.csutil.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utilities to manipulate Java types.
 */
public class TypeUtil {

    /**
     * Map boxed primitives to their corresponding primitive types.
     */
    private static final Map<Class<?>, Class<?>> primitivesByBoxed = new HashMap<>();
    static {
        primitivesByBoxed.put(Boolean.class, Boolean.TYPE);
        primitivesByBoxed.put(Byte.class, Byte.TYPE);
        primitivesByBoxed.put(Character.class, Character.TYPE);
        primitivesByBoxed.put(Double.class, Double.TYPE);
        primitivesByBoxed.put(Float.class, Float.TYPE);
        primitivesByBoxed.put(Integer.class, Integer.TYPE);
        primitivesByBoxed.put(Long.class, Long.TYPE);
        primitivesByBoxed.put(Short.class, Short.TYPE);
    }

    private static final String[] packageWhiteList =
            {
                    "org/csutil/checksum/WrappedChecksumTest"
            };

    private static final String[] ignoredPackages =
            {
                    "java/",
                    "javax/",
                    "jdk/",
                    "sun/",
                    "com/sun/",
                    "junit/",
                    "org/junit/",
                    "org/hamcrest/",
                    "org/objectweb/",
                    "org/csutil/",
                    "org/jacoco/"
            };

    /**
     * Returns an array of all the fields, excluding the fields
     * declared in the super class only if the super class should be
     * ignored.
     * <p>
     * Same as {@code getAllFields(clz, true)}
     * @param clz the class to get all the fields from
     * @return the array of all the fields.
     */
    public static Field[] getAllFields(final Class<?> clz) {
        return getAllFields(clz, true);
    }

    /**
     * Returns an array of Field objects reflecting all the fields
     * declared by the class or interface represented by this Class
     * object and all of its superclasses. The returned array is
     * sorted in a particular order, which is that primitives go first
     * while both primitives or both non-primitives are sorted by the
     * names.
     * @param clz the Class object to get all the fields from
     * @param skipIgnoredSuperClasses if we skip collecting fields
     *                                from ignored super classes.
     * @return the array of Field objects representing all the
     * declared fields of this class and all of its superclasses.
     */
    public static Field[] getAllFields(final Class<?> clz, boolean skipIgnoredSuperClasses) {
        List<Field> fields = new ArrayList<>(Arrays.asList(clz.getDeclaredFields()));
        Class<?> parent = clz.getSuperclass();
        while (parent != null) {
            if (skipIgnoredSuperClasses && isIgnoredClass(parent)) {
                break;
            }
            fields.addAll(Arrays.asList(parent.getDeclaredFields()));
            parent = parent.getSuperclass();
        }
        Field[] fieldsArr = fields.toArray(new Field[0]);
        Arrays.sort(fieldsArr, (f1, f2) -> {
            boolean isf1Primitive = f1.getType().isPrimitive();
            boolean isf2Primitive = f2.getType().isPrimitive();
            if (isf1Primitive && !isf2Primitive) {
                return -1;
            }
            if (!isf1Primitive && isf2Primitive) {
                return 1;
            }
            return f1.getName().compareTo(f2.getName());
        });
        return fieldsArr;
    }

    /**
     * Returns whether the given {@code clz} is one of the container
     * types: array, {@link Collection} and {@link Map}.
     * @param clz the class to query.
     * @return true if the given {@code clz} is a container
     */
    public static boolean isContainer(final Class<?> clz) {
        return clz.isArray()
                || Collection.class.isAssignableFrom(clz)
                || Map.class.isAssignableFrom(clz);
    }

    /**
     * Returns whether the given {@code clz} is of {@link String} type.
     * @param clz the class to query.
     * @return true if the given {@code clz} is of {@link String} type
     */
    public static boolean isString(final Class<?> clz) {
        return clz.equals(String.class);
    }

    /**
     * Returns whether the given {@code clz} is a boxed primitive type
     * ({@link Boolean}, {@link Byte}, {@link Character},
     * {@link Short}, {@link Integer}, {@link Long}, {@link Double},
     * {@link Float}).
     * @param clz the class to query.
     * @return true if the given {@code clz} is a boxed primitive type
     */
    public static boolean isBoxedPrimitive(final Class<?> clz) {
        return primitivesByBoxed.containsKey(clz);
    }

    /**
     * Converts the given boxed primitive to its corresponding
     * primitive type.
     * @param clz the class to convert
     * @return the corresponding primitive type if {@code clz} is a
     * boxed primitive, <b>null</b> otherwise
     */
    public static Class<?> boxedToPrimitive(final Class<?> clz) {
        return primitivesByBoxed.get(clz);
    }

    /**
     * Returns true if we should ignore the given class.
     * @param clz the class to query.
     * @return true if the given class should be ignored
     */
    public static boolean isIgnoredClass(Class<?> clz) {
        return isIgnoredClass(bin2Intern(clz.getName()));
    }

    /**
     * Returns true if we should ignore the given class.
     * @param className the internal name of class to query.
     * @return true if the given class should be ignored
     */
    public static boolean isIgnoredClass(String className) {
        for (String pkgName : packageWhiteList) {
            if (className.startsWith(pkgName)) {
                return false;
            }
        }
        for (String pkgName : ignoredPackages) {
            if (className.startsWith(pkgName)) {
                return true;
            }
        }
        return false;
    }

    public static String intern2Bin(final String internClassName) {
        return internClassName.replace('/', '.');
    }

    public static String bin2Intern(final String binClassName) {
        return binClassName.replace('.', '/');
    }
}
