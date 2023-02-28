package org.csutil.util;

import org.csutil.log.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
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
     * Returns a list of all the fields, excluding the fields
     * declared in the super class only if the super class should be
     * ignored.
     * <p>
     * Same as {@code getAllFields(clz, true)}
     * @param clz the class to get all the fields from
     * @return a list of all the fields, excluding synthetic or static
     * fields, any field that starts with "this*", or any field that
     * are not allowed to access.
     */
    public static List<Field> getAllFields(final Class<?> clz) {
        return getAllFields(clz, true);
    }

    /**
     * Returns a list of Field objects reflecting all the fields
     * declared by the class or interface represented by this Class
     * object and all of its superclasses. The returned array is
     * sorted in a particular order, where the fields declared by
     * subclasses go first; for the fields declared by the same
     * class primitives go first; both primitives or both references
     * are sorted by the names.
     * We ignore synthetic fields, any field that starts with
     * "this$*", or any field that are not allowed to access.
     * Borrowed some design from
     * https://github.com/jdereg/java-util/blob/5c3a9775c11f7942f212bd0256ea4853169c846b/src/main/java/com/cedarsoftware/util/ReflectionUtils.java#L164
     * @param clz the Class object to get all the fields from
     * @param skipIgnoredSuperClasses if we skip collecting fields
     *                                from ignored super classes.
     * @return a list of Field objects representing all the declared
     * fields of this class and all of its superclasses, excluding
     * synthetic or static fields, any field that starts with "this*",
     * or any field that are not allowed to access.
     */
    public static List<Field> getAllFields(final Class<?> clz,
                                           boolean skipIgnoredSuperClasses) {
        List<Field> allFields = new ArrayList<>();
        Class<?> currClz = clz;
        while (currClz != null) {
            if (skipIgnoredSuperClasses && isIgnoredClass(currClz)) {
                break;
            }
            Field[] declaredFields = currClz.getDeclaredFields();
            // Prune declared fields
            List<Field> prunedDeclaredFields = new ArrayList<>();
            for (Field field : declaredFields) {
                if (field.isSynthetic()
                    || field.getName().startsWith("this$")
                    || Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                // TODO: remove this when jdk fixes the issue on
                // getting value of Class#componentType
                if (currClz.getName().equals("java.lang.Class") &&
                        field.getName().equals("componentType")) {
                    continue;
                }
                try {
                    field.setAccessible(true);
                } catch (InaccessibleObjectException | SecurityException ignored) {
                    // ignore the field if it is not allowed to access
                    Log.debug(ignored);
                    Log.debug("Ignore field " + field);
                    continue;
                }
                prunedDeclaredFields.add(field);
            }
            // Sort declared fields
            prunedDeclaredFields.sort((f1, f2) -> {
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

            // Add to the collection
            allFields.addAll(prunedDeclaredFields);
            currClz = currClz.getSuperclass();
        }
        return allFields;
    }

    /**
     * Returns whether the given {@code clz} is one of the container
     * types: array, {@link Collection}, {@link Map} and
     * {@link Map.Entry}.
     * @param clz the class to query.
     * @return true if the given {@code clz} is a container
     */
    public static boolean isContainer(final Class<?> clz) {
        return clz.isArray()
               || Collection.class.isAssignableFrom(clz)
               || Map.class.isAssignableFrom(clz)
               || Map.Entry.class.isAssignableFrom(clz);
    }

    /**
     * Returns whether the given {@code clz} is an immutable type,
     * i.e., primtives, boxed primitives or strings.
     * @param clz the class to query.
     * @return true if the given {@code clz} is an immutable type
     */
    public static boolean isImmutable(final Class<?> clz) {
        return clz.isPrimitive() || isBoxedPrimitive(clz) || isString(clz);
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
