package jattack.bytecode;

import org.objectweb.asm.Type;
import jattack.data.Data;
import jattack.util.TypeUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StaticFieldAnalyzer {

    private final Collection<Class<?>> clzes;

    private Map<Class<?>, Set<Field>> fieldsByClz;

    private Map<String, Set<Field>> fieldsByMethod;

    public StaticFieldAnalyzer(Collection<Class<?>> clzes) {
        this.clzes = clzes;
    }

    public void staticAnalyze() {
        analyzeFieldsAccessibleFromClz();
        analyzeFieldsAccessibleFromMethod();
        analzyeFieldsAccessbileFromHoles();
    }

    private void analzyeFieldsAccessbileFromHoles() {
        for (Map.Entry<String, Set<Field>> e : fieldsByMethod.entrySet()) {
            String method = e.getKey();
            Set<Field> fields = e.getValue();
            // A field that is accessible from a method can be
            // accessed from every hole in the method.
            for (int hole : Data.getHolesOfMethod(method)) {
                for (Field field : fields) {
                    Data.addToVarsByHole(hole, createSymbolFromField(field));
                }
            }
        }
    }

    private void analyzeFieldsAccessibleFromClz() {
        fieldsByClz = new HashMap<>();
        for (Class<?> clz : clzes) {
            analyzeFieldsAccessibleFromClz(clz);
        }
    }

    /**
     * Get all the accessible fields from this class.
     */
    private Set<Field> analyzeFieldsAccessibleFromClz(Class<?> clz) {
        if (fieldsByClz.containsKey(clz)) {
            return fieldsByClz.get(clz);
        }
        Set<Field> fields = new HashSet<>();
        for (Field field : clz.getDeclaredFields()) {
            // Skip any synthetic field or any with non-supported type
            if (field.isSynthetic() || !isTypeSupported(field)) {
                continue;
            }
            fields.add(field);
        }
        // Include static fields from the enclosing class, only
        // when this class is a static nested class.
        // TODO: not support non-static nested class
        if (TypeUtil.isStatic(clz) && clz.getEnclosingClass() != null) {
            for (Field f : analyzeFieldsAccessibleFromClz(clz.getEnclosingClass())) {
                if (TypeUtil.isStatic(f)) {
                    fields.add(f);
                }
            }
        }
        fieldsByClz.put(clz, fields);
        return fields;
    }

    private void analyzeFieldsAccessibleFromMethod() {
        fieldsByMethod = new HashMap<>();
        for (Class<?> clz : clzes) {
            analyzeFieldsAccessibleFromMethod(clz);
        }
    }

    /**
     * Get all the accessible fields from every method for this class.
     */
    private void analyzeFieldsAccessibleFromMethod(Class<?> clz) {
        // Analyze accessible fields from every method
        String className = clz.getName();
        for (Method method : clz.getDeclaredMethods()) {
            String name = method.getName();
            String desc = Type.getMethodDescriptor(method);
            String fullMethodName = TypeUtil.toFullMethodName(
                    className, name, desc
            );
            if (!Data.hasHoleInMethod(fullMethodName)) {
                // we don't care the methods without holes inside.
                continue;
            }
            Set<Field> accessibleFields = new HashSet<>();
            boolean isStaticMethod = TypeUtil.isStatic(method);
            for (Field field : fieldsByClz.get(clz)) {
                if (TypeUtil.isStatic(field)) {
                    accessibleFields.add(field);
                }
                if (isStaticMethod) {
                    continue;
                }
                // only non-static method can access non-static field
                accessibleFields.add(field);
            }
            fieldsByMethod.put(fullMethodName, accessibleFields);
        }
    }

    private static boolean isTypeSupported(Field field) {
        String desc = Type.getDescriptor(field.getType());
        return TypeUtil.isTypeDescSupported(desc);
    }

    private static Symbol createSymbolFromField(Field field) {
        Class<?> type = field.getType();
        String desc = Type.getDescriptor(type);
        if (type.isPrimitive()) {
            // wrap primitives
            desc = TypeUtil.primitiveDescToWrappedDesc(desc);
        }
        return new Symbol(field.getName(), desc);
    }
}