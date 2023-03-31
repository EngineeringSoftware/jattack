package jattack.bytecode;

import jattack.data.Data;
import jattack.util.TypeUtil;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Find, save and update all the available fields given an object or
 * class.
 */
public class FieldAnalyzer {

    private static class Fld {
        private final Field field;
        /**
         * Object from which the represented field's value is to be
         * extracted, which could be null for static fields.
         */
        private final Object obj;
        private Fld(Field field, Object obj) {
            this.field = field;
            field.setAccessible(true);
            this.obj = obj;
        }
    }

    private final Set<Fld> flds;

    private static FieldAnalyzer fieldAnalyzer;

    // cache fields for every class to speed up repeated getting
    private static Map<Class<?>, Set<Field>> fieldsByClz;
    private static Map<Class<?>, Set<Field>> staticFieldsByClz;

    private FieldAnalyzer() {
        flds = new HashSet<>();
    }

    /**
     * Initialize a field analyzer.
     */
    private static void initFieldAnalyzer() {
        fieldAnalyzer = new FieldAnalyzer();
    }

    /**
     * Find all the available fields from the context of an instance
     * method and save the values of those fields.
     * <p>
     * clz does not have to be obj.getClass() because we might be
     * looking for available fields in a superclass while using an
     * object of subclass, where obj.getClass() is the subclass while
     * clz is the superclass.
     * <p>
     * Used through instrumentation.
     */
    public static void findAndSaveFieldsForObject(Object obj, Class<?> clz) {
        initFieldAnalyzer();
        Set<Field> fields = collectFieldsForSingleClz(clz);
        createFlds(fields, obj);
        saveFieldValues();
    }

    /**
     * Find all the available fields from the context of a static
     * method and save the values of those fields.
     * <p>
     * Used through instrumentation.
     */
    public static void findAndSaveFieldsForClass(Class<?> clz) {
        initFieldAnalyzer();
        Set<Field> fields = collectStaticFieldsForSingleClz(clz);
        createFlds(fields, null);
        saveFieldValues();
    }

    private static void createFlds(Set<Field> fields, Object obj) {
        for (Field f : fields) {
            if (Data.memoryContainsSymbol(f.getName())) {
                // TODO: shadowing? For now we ignore the field if it
                // is shadowed.
                continue;
            }
            fieldAnalyzer.flds.add(new Fld(f, obj));
        }
    }

    /**
     * Save the values of all the available fields.
     */
    private static void saveFieldValues() {
        fieldAnalyzer.saveFieldValues0();
    }

    /**
     * Update the values of all the available fields.
     * <p>
     * Used through instrumentation.
     */
    public static void updateFieldValues() {
        fieldAnalyzer.updateFieldValues0();

        // It makes more sense to put this line as an inserted
        // instruction after eval() but putting here saves some bytes
        // from MethodTooLargeException for us.
        Data.resetMemory(); // Release references to help GC
    }

    private void saveFieldValues0() {
        for (Fld fld : flds) {
            Field field = fld.field;
            Object obj = fld.obj;
            try {
                String name = field.getName();
                String desc = TypeUtil.bin2Desc(field.getType().getName());
                Object value = field.get(obj);
                Data.addToMemory(name, desc, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateFieldValues0() {
        for (Fld fld : flds) {
            Field field = fld.field;
            if (TypeUtil.isFinal(field)) {
                // We cannot modify a final field
                continue;
            }
            Object obj = fld.obj;
            Object val = Data.getFromMemoryValueOfSymbol(field.getName());
            try {
                field.set(obj, val);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get all the accessible static fields from this class, lazily
     * using cache {@link FieldAnalyzer#fieldsByClz} and
     * {@link FieldAnalyzer#staticFieldsByClz}.
     */
    private static Set<Field> collectStaticFieldsForSingleClz(Class<?> clz) {
        if (staticFieldsByClz == null) {
            staticFieldsByClz = new HashMap<>();
        }
        if (staticFieldsByClz.containsKey(clz)) {
            return staticFieldsByClz.get(clz);
        }
        Set<Field> staticFields = collectFieldsForSingleClz(clz).stream()
                .filter(TypeUtil::isStatic)
                .collect(Collectors.toCollection(HashSet::new));
        staticFieldsByClz.put(clz, staticFields);
        return staticFields;
    }

    /**
     * Get all the accessible fields from this class, lazily
     * using cache {@link FieldAnalyzer#fieldsByClz}.
     */
    private static Set<Field> collectFieldsForSingleClz(Class<?> clz) {
        if (fieldsByClz == null) {
            fieldsByClz = new HashMap<>();
        }
        if (fieldsByClz.containsKey(clz)) {
            return fieldsByClz.get(clz);
        }
        Set<Field> fields = new HashSet<>();
        for (Field f : clz.getDeclaredFields()) {
            // Skip any synthetic field or any with non-supported type
            if (f.isSynthetic() || !TypeUtil.isTypeSupported(f)) {
                continue;
            }
            fields.add(f);
        }
        // Include static fields from the enclosing class, only
        // when this class is a static nested class.
        // TODO: support non-static nested class
        if (TypeUtil.isStatic(clz) && clz.getEnclosingClass() != null) {
            for (Field f : collectFieldsForSingleClz(clz.getEnclosingClass())) {
                if (TypeUtil.isStatic(f)) {
                    fields.add(f);
                }
            }
        }
        fieldsByClz.put(clz, fields);
        return fields;
    }
}
