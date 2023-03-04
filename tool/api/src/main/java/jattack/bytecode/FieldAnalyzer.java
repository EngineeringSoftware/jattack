package jattack.bytecode;

import jattack.data.Data;
import jattack.util.TypeUtil;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * Find, save and update all the available fields given an object or
 * class.
 */
public class FieldAnalyzer {

    private static class Fld {
        private final Field field;
        private final Object obj;
        private Fld(Field field, Object obj) {
            this.field = field;
            field.setAccessible(true);
            this.obj = obj;
        }
    }

    private final Set<Fld> flds;

    private static FieldAnalyzer fieldAnalyzer;

    private FieldAnalyzer() {
        flds = new HashSet<>();
    }

    /**
     * Initialize a field analyzer.
     * <p>
     * Used through instrumentation.
     */
    public static void initFieldAnalyzer() {
        fieldAnalyzer = new FieldAnalyzer();
    }

    /**
     * Find all the available fields from the context of an instance
     * method.
     * <p>
     * Used through instrumentation.
     */
    public static void findFields(Object obj) {
        fieldAnalyzer.findFields(obj, null);
    }

    /**
     * Find all the available fields from the context of a static
     * method.
     * <p>
     * Used through instrumentation.
     */
    public static void findFields(Class<?> clz) {
       fieldAnalyzer.findFields(null, clz);
    }

    /**
     * Save the values of all the available fields.
     * <p>
     * Used through instrumentation.
     */
    public static void saveFieldValues() {
        fieldAnalyzer.saveFieldValues0();
    }

    /**
     * Update the values of all the available fields.
     * <p>
     * Used through instrumentation.
     */
    public static void updateFieldValues() {
        fieldAnalyzer.updateFieldValues0();
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

    private void findFields(Object obj, Class<?> clz) {
        if (obj == null && clz == null
                || obj != null && clz != null) {
            throw new IllegalArgumentException("Either obj or clz but not both has to be null!");
        }

        boolean fromStaticMethod = true;
        if (obj != null) {
            // non-static method
            clz = obj.getClass();
            fromStaticMethod = false;
        }

        // Find the fields declared by clz
        findFieldsForSelf(obj, clz, fromStaticMethod);

        // Find the fields declared by any enclosing class
        if (!TypeUtil.isStatic(clz)) {
            // TODO: Support getting fields from the enclosing class
            //  of a non-static nested class.
            return;
        }
        clz = clz.getEnclosingClass();
        while (clz != null) {
            findFieldsForEnclosingClass(obj, clz);
            clz = clz.getEnclosingClass();
        }
    }

    private void findFieldsForSelf(
            Object obj,
            Class<?> clz,
            boolean fromStaticMethod) {
        for (Field field : clz.getDeclaredFields()) {
            // Skip any synthetic field
            if (field.isSynthetic()) {
                continue;
            }

            // Check syntactically access from the context of eval()
            // TODO: Instead of hardcode here, can we determine if the
            //  field is accessible from the context that it will be
            //  filled at runtime?
            if (fromStaticMethod && !TypeUtil.isStatic(field)) {
                // Cannot access a non-static field from a static
                // method
                continue;
            }

            // Save the field.
            String name = field.getName();
            if (Data.memoryContainsSymbol(name)) {
                // TODO: shadowing? For now we assume no shadowing
                //  happens. We ignore the field if it is
                //  shadowed.
                continue;
            }
            flds.add(new Fld(field, obj));
        }
    }

    private void findFieldsForEnclosingClass(
            Object obj,
            Class<?> clz) {
        for (Field field : clz.getDeclaredFields()) {
            if (field.isSynthetic()) {
                continue;
            }

            // Static nested class can access only static fields of
            // the enclosing class.
            if (!TypeUtil.isStatic(field)) {
                continue;
            }

            // Save the field
            flds.add(new Fld(field, obj));
        }
    }
}
