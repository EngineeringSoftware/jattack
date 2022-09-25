package org.csutil.checksum;

import org.csutil.log.Log;
import org.csutil.util.ByteBufferUtil;
import org.csutil.util.TypeUtil;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.CRC32;

/**
 * Wrapped Checksum class that support hash any type.
 */
public class WrappedChecksum {

    // An Adler-32 checksum is almost as reliable as a CRC-32 but can
    // be computed much faster. We can consider using Adler32 instead.
    private final CRC32 checksum;

    /**
     * Whether we exclude Collection and Map in checksum.
     */
    private final boolean skipCollectionAndMap;

    public WrappedChecksum() {
        this(true);
    }

    /**
     * Constructor.
     */
    public WrappedChecksum(boolean skipCollectionAndMap) {
        checksum = new CRC32();
        this.skipCollectionAndMap = skipCollectionAndMap;
    }

    /**
     * Returns the current checksum value.
     * @return the current checksum value
     */
    public long getValue() {
        return checksum.getValue();
    }

    /**
     * Resets checksum to initial value.
     */
    public void reset() {
        Log.debug("RESET checksum");
        checksum.reset();
    }

    /**
     * Update the current checksum with the given boolean.
     * @param value the boolean to update the checksum with
     */
    public void update(boolean value) {
        Log.debug("Checksum boolean " + value);
        checksum.update(ByteBufferUtil.toByteBuffer(value));
    }

    /**
     * Update the current checksum with the given byte.
     * @param value the byte to update the checksum with
     */
    public void update(byte value) {
        Log.debug("Checksum byte " + value);
        checksum.update(ByteBufferUtil.toByteBuffer(value));
    }

    /**
     * Update the current checksum with the given char.
     * @param value the char to update the checksum with
     */
    public void update(char value) {
        Log.debug("Checksum char " + value);
        checksum.update(ByteBufferUtil.toByteBuffer(value));
    }

    /**
     * Update the current checksum with the given double.
     * @param value the double to update the checksum with
     */
    public void update(double value) {
        Log.debug("Checksum double " + value);
        checksum.update(ByteBufferUtil.toByteBuffer(value));
    }

    /**
     * Update the current checksum with the given float.
     * @param value the float to update the checksum with
     */
    public void update(float value) {
        Log.debug("Checksum float " + value);
        checksum.update(ByteBufferUtil.toByteBuffer(value));
    }

    /**
     * Update the current checksum with the given integer.
     * @param value the integer to update the checksum with
     */
    public void update(int value) {
        Log.debug("Checksum int " + value);
        checksum.update(ByteBufferUtil.toByteBuffer(value));
    }

    /**
     * Update the current checksum with the given long.
     * @param value the long to update the checksum with
     */
    public void update(long value) {
        Log.debug("Checksum long " + value);
        checksum.update(ByteBufferUtil.toByteBuffer(value));
    }

    /**
     * Update the current checksum with the given short.
     * @param value the short to update the checksum with
     */
    public void update(short value) {
        Log.debug("Checksum short " + value);
        checksum.update(ByteBufferUtil.toByteBuffer(value));
    }

    /**
     * Update the current checksum with the given object.
     * @param obj the object to update the checksum with
     */
    public void update(Object obj) {
        if (obj == null) {
            updateNull();
            return;
        }

        Class<?> clz = obj.getClass();
        if (clz.isPrimitive() || TypeUtil.isBoxedPrimitive(clz)) {
            // Boxed primitives
            updateBoxedPrimitive(obj);
        } else if (TypeUtil.isString(clz)) {
            updateString((String) obj);
        } else if (TypeUtil.isContainer(clz)) {
            updateContainer(obj);
        } else {
            if (!TypeUtil.isIgnoredClass(clz)) {
                // Other reference types
                updateObject(obj);
            }
        }
    }

    /**
     * Update the current checksum with the given container object.
     * @param obj the container object to update the checksum with
     */
    private void updateContainer(Object obj) {
        Class<?> clz = obj.getClass();
        if (obj.getClass().isArray()) {
            for (int i = 0; i < Array.getLength(obj); ++i) {
                update(Array.get(obj, i));
            }
        }
        if (!skipCollectionAndMap) {
            if (obj instanceof Collection) {
                for (Object e : (Collection) obj) {
                    update(e);
                }
            }
            if (obj instanceof Map) {
                for (Object e : ((Map) obj).entrySet()) {
                    update(((Map.Entry) e).getKey());
                    update(((Map.Entry) e).getValue());
                }
            }
        }
    }

    /**
     * Update the current checksum with the given object that is not
     * a boxed primitive or a String or a container.
     * <p>
     * We essentially encode the shape of the object graph. For any
     * primitive or boxed primitive field, we update the current
     * checksum with the value of the field. For any object in the
     * graph, we set a unique id along a DFS and update the checksum
     * with the unique id.
     * @param obj the object to update the checksum with
     */
    private void updateObject(Object obj) {
        // Set of hashcode of objects we update the checksum with
        Set<Integer> visited = new HashSet<>();
        // Mapping from hashcode of objects to a unique id since
        // hashcode varies across runs.
        Map<Integer, Integer> idsByObj = new HashMap<>();
        idsByObj.put(System.identityHashCode(obj), idsByObj.size());
        updateObject(obj, visited, idsByObj);
    }

    private void updateObject(
            Object obj,
            Set<Integer> checksumed,
            Map<Integer, Integer> idsByObj) {
        int hashcode = System.identityHashCode(obj);
        if (checksumed.contains(hashcode)) {
            // Avoid revisiting a checksumed object
            return;
        }
        checksumed.add(hashcode); // Mark visited

        // Checksum every and each field
        Class<?> clz = obj.getClass();
        Log.debug("Start checksuming object " + clz.getName());
        for (Field field : TypeUtil.getAllFields(clz)) {
            if (field.isSynthetic()) {
                continue; // skip synthetic field is a good habit
            }
            field.setAccessible(true);
            try {
                Log.debug("Start checksuming field " + clz.getName() + ":" + field.getName());
                Object fieldVal = field.get(obj);
                updateField(fieldVal, checksumed, idsByObj);
                Log.debug("End checksuming field " + clz.getName() + ":" + field.getName());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        Log.debug("End checksuming object " + clz.getName());
    }

    /**
     * Update the current checksum with the given field.
     */
    private void updateField(
            Object obj,
            Set<Integer> checksumed,
            Map<Integer, Integer> idsByObj) {
        if (obj == null) {
            updateNull();
            return;
        }
        Class<?> clz = obj.getClass();
        if (clz.isPrimitive() || TypeUtil.isBoxedPrimitive(clz)) {
            updateBoxedPrimitive(obj);
        } else if (TypeUtil.isString(clz)) {
            updateString((String) obj);
        } else if (TypeUtil.isContainer(clz)) {
            updateContainerAsField(obj, checksumed, idsByObj);
        } else {
            // Update the checksum with the unique id of this
            // adjacent object field
            int adjHashcode = System.identityHashCode(obj);
            // Get the unique id for this adjacent object field
            int adjUniqId = idsByObj.getOrDefault(adjHashcode, idsByObj.size());
            // Put (hashcode, uniqueId) pair into map
            idsByObj.putIfAbsent(adjHashcode, adjUniqId);
            // Update the checksum with the unique id
            update(adjUniqId);
            // Update the checksum with the adjacent object value
            // only when the field is not of ignored type
            if (!TypeUtil.isIgnoredClass(clz)) {
                // DFS traversal
                updateObject(obj, checksumed, idsByObj);
            } else {
                Log.debug("Ignore " + clz.getName());
            }
        }
    }

    // TODO: merge into updateContainer(Object)
    private void updateContainerAsField(
            Object obj,
            Set<Integer> checksumed,
            Map<Integer, Integer> idsByObj) {
        if (obj.getClass().isArray()) {
            Log.debug("Start checksuming array " + obj.getClass().getName());
            for (int i = 0; i < Array.getLength(obj); ++i) {
                updateField(Array.get(obj, i), checksumed, idsByObj);
            }
            Log.debug("End checksuming array " + obj.getClass().getName());
        }
        if (!skipCollectionAndMap) {
            if (obj instanceof Collection) {
                for (Object e : (Collection) obj) {
                    updateField(e, checksumed, idsByObj);
                }
            }
            if (obj instanceof Map) {
                for (Object e : ((Map) obj).entrySet()) {
                    updateField(((Map.Entry) e).getKey(), checksumed, idsByObj);
                    updateField(((Map.Entry) e).getValue(), checksumed, idsByObj);
                }
            }
        }
    }

    /**
     * Update the current checksum with the given {@link String}.
     * @param str the {@link String} to update the checksum with
     */
    private void updateString(String str) {
        Log.debug("Checksum String " + str);
        for (char c : str.toCharArray()) {
            update(c);
        }
    }

    /**
     * Update the current checksum with the given boxed primitive.
     * @param obj the boxed primitive to update the checksum
     *            with
     */
    private void updateBoxedPrimitive(Object obj) {
        String className = obj.getClass().getCanonicalName();
        switch (className) {
        case "java.lang.Boolean": {
            boolean value = ((Boolean) obj).booleanValue();
            update(value);
            break;
        }
        case "java.lang.Byte": {
            byte value = ((Byte) obj).byteValue();
            update(value);
            break;
        }
        case "java.lang.Character": {
            char value = ((Character) obj).charValue();
            update(value);
            break;
        }
        case "java.lang.Double": {
            double value = ((Double) obj).doubleValue();
            update(value);
            break;
        }
        case "java.lang.Float": {
            float value = ((Float) obj).floatValue();
            update(value);
            break;
        }
        case "java.lang.Integer": {
            int value = ((Integer) obj).intValue();
            update(value);
            break;
        }
        case "java.lang.Long": {
            long value = ((Long) obj).longValue();
            update(value);
            break;
        }
        case "java.lang.Short": {
            short value = ((Short) obj).shortValue();
            update(value);
            break;
        }
        default:
            throw new RuntimeException("Unexpected boxed types: " +
                    className + "!");
        }
    }

    /**
     * Update the current checksum with null.
     */
    private void updateNull() {
        Log.debug("Checksum NULL");
        updateString("null");
    }

    /**
     * Update the current checksum with all static fields of the given
     * class.
     * @param clz the class to update the checksum with
     */
    public void updateStaticFieldsOfClass(Class<?> clz) {
        for (Field field : clz.getDeclaredFields()) {
            if (!field.isSynthetic()
                    && Modifier.isStatic(field.getModifiers())) {
                // Get static field value and use the value to update
                // checksum.
                field.setAccessible(true);
                try {
                    update(field.get(null));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
