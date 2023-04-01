package org.csutil.checksum;

import org.csutil.log.Log;
import org.csutil.util.ByteBufferUtil;
import org.csutil.util.TypeUtil;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * Wrapped Checksum class that support hash any type.
 * NOTE: @{code null} is hashed as the same as {@code "null"};
 *      all NaN of double is hashed as the same as the canonical one
 *      {@link Double#NaN);
 *      all NaN of float is hashed as the same as the canonical one
 *      {@link Float#NaN}.
 */
public class WrappedChecksum {

    private final Checksum checksum;
    private final boolean ignoreJavaClasses;

    /**
     * Constructor.
     */
    public WrappedChecksum() {
        this(false);
    }

    public WrappedChecksum(boolean ignoreJavaClasses) {
        checksum = newChecksum();
        this.ignoreJavaClasses = ignoreJavaClasses;
    }

    /**
     * Factory to produce a new {@link Checksum} instance.
     * @return a new {@link Checksum} instance.
     */
    public static Checksum newChecksum() {
        // An Adler-32 checksum is almost as reliable as a CRC-32 but
        // can be computed much faster. We can consider using Adler32
        // instead.
        return new CRC32();
    }

    /**
     * Returns the current checksum value.
     * @return the current checksum value
     */
    public long getValue() {
        return checksum.getValue();
    }

    /**
     * Returns whether the checksum ignores java stand library classes
     * and some other classes, as defined in
     * {@link TypeUtil#isIgnoredClass(Class)}.
     * @return true if the checksum ignores java stand library classes
     *         and some other classes, as defined in
     *         {@link TypeUtil#isIgnoredClass(Class)}.
     */
    public boolean isIgnoreJavaClasses() {
        return ignoreJavaClasses;
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
        updateBoolean(value, checksum);
    }

    private static void updateBoolean(boolean value, Checksum checksum) {
        Log.debug("Checksum boolean " + value);
        checksum.update(ByteBufferUtil.toByteBuffer(value));
    }

    /**
     * Update the current checksum with the given byte.
     * @param value the byte to update the checksum with
     */
    public void update(byte value) {
        updateByte(value, checksum);
    }

    private static void updateByte(byte value, Checksum checksum) {
        Log.debug("Checksum byte " + value);
        checksum.update(ByteBufferUtil.toByteBuffer(value));
    }

    /**
     * Update the current checksum with the given char.
     * @param value the char to update the checksum with
     */
    public void update(char value) {
        updateChar(value, checksum);
    }

    private static void updateChar(char value, Checksum checksum) {
        Log.debug("Checksum char " + value);
        checksum.update(ByteBufferUtil.toByteBuffer(value));
    }

    /**
     * Update the current checksum with the given double.
     * @param value the double to update the checksum with
     */
    public void update(double value) {
        updateDouble(value, checksum);
    }

    private static void updateDouble(double value, Checksum checksum) {
        Log.debug("Checksum double " + value);
        checksum.update(ByteBufferUtil.toByteBuffer(value));
    }

    /**
     * Update the current checksum with the given float.
     * @param value the float to update the checksum with
     */
    public void update(float value) {
        updateFloat(value, checksum);
    }

    private static void updateFloat(float value, Checksum checksum) {
        Log.debug("Checksum float " + value);
        checksum.update(ByteBufferUtil.toByteBuffer(value));
    }

    /**
     * Update the current checksum with the given integer.
     * @param value the integer to update the checksum with
     */
    public void update(int value) {
        updateInt(value, checksum);
    }

    private static void updateInt(int value, Checksum checksum) {
        Log.debug("Checksum int " + value);
        checksum.update(ByteBufferUtil.toByteBuffer(value));
    }

    /**
     * Update the current checksum with the given long.
     * @param value the long to update the checksum with
     */
    public void update(long value) {
        updateLong(value, checksum);
    }

    private static void updateLong(long value, Checksum checksum) {
        Log.debug("Checksum long " + value);
        checksum.update(ByteBufferUtil.toByteBuffer(value));
    }

    /**
     * Update the current checksum with the given short.
     * @param value the short to update the checksum with
     */
    public void update(short value) {
        updateShort(value, checksum);
    }

    private static void updateShort(short value, Checksum checksum) {
        Log.debug("Checksum short " + value);
        checksum.update(ByteBufferUtil.toByteBuffer(value));
    }

    /**
     * Update the current checksum with the given object.
     * @param object the object to update the checksum with
     */
    public void update(Object object) {
        updateObject(object, checksum, ignoreJavaClasses);
    }

    private static void updateObject(
            Object object,
            Checksum checksum,
            boolean ignoreJavaClasses) {
        // We allow revisiting the same primitives and strings
        // but not containers or other objects.
        if (isNullOrBoxedPrimitiveOrString(object)) {
            updateNullOrBoxedPrimitivesOrString(object, checksum);
            return;
        }
        // other objects
        // objects to be used to update the checksum, containing only
        // objects that are not (boxed) primitives or strings
        Deque<ObjectWithHash> stack = new ArrayDeque<>();
        // objects that have been visited
        Set<ObjectWithHash> visited = new HashSet<>();
        // Mapping from Objects to a unique id since hashcode varies
        // across runs.
        Map<ObjectWithHash, Integer> idsByObj = new HashMap<>();
        pushObjectOntoStack(stack, visited, idsByObj, object, checksum, ignoreJavaClasses);

        // Checksum we are currently using
        Deque<ChecksumStackLevel> checksumStack = new ArrayDeque<>();
        checksumStack.push(new ChecksumStackLevel(checksum, idsByObj, null));

        while (!stack.isEmpty()) {
            ObjectWithHash objWH = stack.peek();
            Object obj = objWH.obj;
            ChecksumStackLevel currChecksumStackLevel = checksumStack.peek();
            Checksum currChecksum = currChecksumStackLevel.checksum;
            Map<ObjectWithHash, Integer> currIdsByObj = currChecksumStackLevel.idsByObj;
            if (isNullOrBoxedPrimitiveOrString(obj)) {
                updateNullOrBoxedPrimitivesOrString(obj, currChecksum);
                if (objWH.isElementOfUnorderedContainer) {
                    ChecksumStackLevel checksumStackLevel = checksumStack.pop();
                    // add hash to sum of enclosing container
                    Log.debug("Save the hash of element into sum");
                    checksumStackLevel.containerLevel.incSum(checksumStackLevel.getValue());
                }
                stack.pop();
                continue;
            }
            if (visited.contains(objWH)) {
                if (!objWH.startedChecksum) {
                    // We just need to encode the edge but should not
                    // explore the object anymore
                    Log.debug("Second edge to " + objWH.obj.getClass().getName());
                    updateObjectToObjectEdge(currIdsByObj, objWH, currChecksum);
                    stack.pop();
                    continue;
                }
                // Seeing an object again means we finished work on
                // the object
                if (objWH.isUnorderedContainer) {
                    // update checksum with the sum of independent
                    // hash of each element in the container
                    Log.debug("Update checksum with sum");
                    currChecksumStackLevel.updateChecksumWithSum();
                }
                if (objWH.isElementOfUnorderedContainer) {
                    ChecksumStackLevel checksumStackLevel = checksumStack.pop();
                    // add hash to sum of enclosing container
                    Log.debug("Save the hash of element into sum");
                    checksumStackLevel.containerLevel.incSum(checksumStackLevel.getValue());
                }
                // Done with the object so we can pop it out
                Log.debug("Done with object " + currIdsByObj.get(objWH) + objWH.obj.getClass().getName());
                stack.pop();
                continue;
            }
            // update the checksum with the edge
            updateObjectToObjectEdge(currIdsByObj, objWH, currChecksum);
            // mark visited
            visited.add(objWH);
            objWH.startedChecksum = true;
            if (isContainer(obj)) {
                updateContainer(stack, visited, currIdsByObj,
                                checksumStack, objWH, currChecksum,
                                ignoreJavaClasses);
            } else {
                updateOtherObject(stack, visited, currIdsByObj, objWH,
                                  currChecksum, ignoreJavaClasses);
            }
        }
    }

    /**
     * Push the given object onto the given stack.
     * @param stack the stack to store all the objects to update
     *              the checksum with
     * @param visited the set of objects that have been used to update
     *                the checksum with
     * @param obj the object to update the checksum with
     */
    private static void pushObjectOntoStack(Deque<ObjectWithHash> stack,
                                            Set<ObjectWithHash> visited,
                                            Map<ObjectWithHash, Integer> idsByObj,
                                            Object obj,
                                            Checksum checksum,
                                            boolean ignoreJavaClasses) {
        pushObjectOntoStack(stack, visited, idsByObj, obj, checksum, ignoreJavaClasses, false);
    }

    private static void pushObjectOntoStack(Deque<ObjectWithHash> stack,
                                            Set<ObjectWithHash> visited,
                                            Map<ObjectWithHash, Integer> idsByObj,
                                            Object obj,
                                            Checksum checksum,
                                            boolean ignoreJavaClasses,
                                            boolean isElementOfUnorderedContainer) {
        if (isNullOrBoxedPrimitiveOrString(obj)) {
            stack.push(new ObjectWithHash(obj, isElementOfUnorderedContainer));
            return;
        }
        ObjectWithHash objWH = new ObjectWithHash(obj, isElementOfUnorderedContainer);
        if (ignoreJavaClasses && isIgnored(obj)) {
            // add edge now because we did not have a chance to encode
            // the edge into checksum later
            updateObjectToObjectEdge(idsByObj, objWH, checksum);
            // Avoid visiting any ignored types
            Log.debug("Ignore " + obj.getClass().getName());
            return;
        }
        // Set flag
        objWH.isUnorderedContainer = isUnorderedContainer(obj);
        stack.push(objWH);
    }

    /**
     * Update the current checksum with the given container object.
     * @param stack the stack to store all the objects to update
     *              the checksum with
     * @param visited the set of objects that have been used to update
     *                the checksum with
     * @param objWH the container object to update the checksum with
     */
    private static void updateContainer(Deque<ObjectWithHash> stack,
                                        Set<ObjectWithHash> visited,
                                        Map<ObjectWithHash, Integer> idsByObj,
                                        Deque<ChecksumStackLevel> checksumStack,
                                        ObjectWithHash objWH,
                                        Checksum checksum,
                                        boolean ignoreJavaClasses) {
        Object container = objWH.obj;
        // Array
        if (container.getClass().isArray()) {
            Log.debug("Checksum array " + container.getClass().getName());
            // We reverse the order of elements when pushing onto
            // the stack
            LinkedList<Object> revStack = new LinkedList<>();
            for (int i = 0; i < Array.getLength(container); ++i) {
                Object e = Array.get(container, i);
                revStack.push(e);
            }
            while (!revStack.isEmpty()) {
                Object elem = revStack.pop();
                pushObjectOntoStack(stack, visited, idsByObj, elem, checksum, ignoreJavaClasses);
            }
            return;
        }
        // Collection
        if (container instanceof Collection) {
            if (container instanceof Set && !(container instanceof SortedSet)) {
                Log.debug("Checksum unordered collection " + container.getClass().getName());
                // We do not update the running checksum because
                // the iterative order is undefined and could vary
                // between different JDK implementations. Thus,
                // we compute independent checksum for each
                // element and simply sum all of them to avoid
                // being affected by the undefined iterative
                // order.
                ChecksumStackLevel containerLevel = checksumStack.peek();
                for (Object e : (Collection<?>) container) {
                    pushObjectOntoStack(stack, visited, idsByObj, e, checksum, ignoreJavaClasses, true);
                    // push a new checksum stack level for each element
                    // because we want to independently compute hash
                    // of each element
                    checksumStack.push(new ChecksumStackLevel(containerLevel));
                }
                return;
            }
            Log.debug("Checksum ordered collection " + container.getClass().getName());
            // Ordered collection
            // We use another stack to reverse the collection
            // we use linkedlist because linkedlist supports null
            LinkedList<Object> revStack = new LinkedList<>();
            for (Object e : (Collection<?>) container) {
                revStack.push(e);
            }
            while (!revStack.isEmpty()) {
                Object elem = revStack.pop();
                pushObjectOntoStack(stack, visited, idsByObj, elem, checksum, ignoreJavaClasses);
            }
            return;
        }
        // Map
        if (container instanceof Map) {
            if (!(container instanceof SortedMap)) {
                Log.debug("Checksum unordered map " + container.getClass().getName());
                // We do not update the running checksum because
                // the iterative order is undefined and could vary
                // between different JDK implementations. Thus,
                // we compute independent checksum for each
                // element and simply sum all of them to avoid
                // being affected by the undefined iterative
                // order.
                ChecksumStackLevel containerLevel = checksumStack.peek();
                for (Map.Entry<?, ?> e : ((Map<?, ?>) container).entrySet()) {
                    pushObjectOntoStack(stack, visited, idsByObj, e, checksum, ignoreJavaClasses, true);
                    // push a new checksum stack level for each element
                    // because we want to independently compute hash
                    // of each element
                    checksumStack.push(new ChecksumStackLevel(containerLevel));
                }
                return;
            }
            Log.debug("Checksum ordered map " + container.getClass().getName());
            // Ordered map
            // We use another stack to reverse the collection
            // we use linkedlist because linkedlist supports null
            LinkedList<Object> revStack = new LinkedList<>();
            for (Map.Entry<?, ?> e : ((Map<?, ?>) container).entrySet()) {
                revStack.push(e);
            }
            while (!revStack.isEmpty()) {
                Object elem = revStack.pop();
                pushObjectOntoStack(stack, visited, idsByObj, elem, checksum, ignoreJavaClasses);
            }
            return;
        }
        if (container instanceof Map.Entry) {
            updateMapEntry(stack, visited, idsByObj, objWH, checksum, ignoreJavaClasses);
            return;
        }
    }

    private static void updateMapEntry(Deque<ObjectWithHash> stack,
                                       Set<ObjectWithHash> visited,
                                       Map<ObjectWithHash, Integer> idsByObj,
                                       ObjectWithHash objWH,
                                       Checksum checksum,
                                       boolean ignoreJavaClasses) {
        Map.Entry<?, ?> entry = (Map.Entry<?, ?>) objWH.obj;
        Log.debug("Checksum map entry " + entry.getClass().getName());
        // We push value then key, so key goes first when popped out
        pushObjectOntoStack(stack, visited, idsByObj, entry.getValue(), checksum, ignoreJavaClasses);
        pushObjectOntoStack(stack, visited, idsByObj, entry.getKey(), checksum, ignoreJavaClasses);
    }

    /**
     * Update the current checksum with the given object that is not
     * a boxed primitive or a String or a container.
     * <p>
     * We essentially encode the shape of the object graph. For any
     * primitive or boxed primitive field or string, we update the
     * current checksum with the value of the field. For any object in
     * the graph, we set a unique id along a DFS and update the
     * checksum with the unique id.
     * @param objWH the object to update the checksum with
     */
    private static void updateOtherObject(Deque<ObjectWithHash> stack,
                                          Set<ObjectWithHash> visited,
                                          Map<ObjectWithHash, Integer> idsByObj,
                                          ObjectWithHash objWH,
                                          Checksum checksum,
                                          boolean ignoreJavaClasses) {
        Object obj = objWH.obj;
        Class<?> clz = obj.getClass();
        Log.debug("Checksum object " + idsByObj.get(objWH) + " " + clz.getName());

        // Checksum every and each field
        // We reverse the order of fields before pushing onto the stack
        // We use another stack to reverse the collection
        // we use linkedlist because linkedlist supports null
        LinkedList<Object> revStack = new LinkedList<>();
        List<Field> allFields = TypeUtil.getAllFields(clz, ignoreJavaClasses);
        for (Field field : allFields) {
            field.setAccessible(true);
            try {
                Object fieldObj = field.get(obj);
                Log.debug("Pushing field: " + field);
                revStack.push(fieldObj);
            } catch (IllegalArgumentException e) {
                // ignore, some internal fields, e.g.,
                // java.lang.Class.componentType is not allowed to
                // obtain value when the Class is not an array
                // type.
                Log.debug(e);
                Log.debug("Ignore field " + field);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        while (!revStack.isEmpty()) {
            Object elem = revStack.pop();
            pushObjectOntoStack(stack, visited, idsByObj, elem, checksum, ignoreJavaClasses);
        }
    }

    /**
     * Update the checksum with the edge between two objects if the
     * object is not (boxed) primitive or string.
     */
    private static void updateObjectToObjectEdge(Map<ObjectWithHash, Integer> idsByObj, ObjectWithHash objWH, Checksum checksum) {
        int uid = idsByObj.getOrDefault(objWH, idsByObj.size());
        idsByObj.putIfAbsent(objWH, uid);
        // TODO: could hash more than uid?
        Log.debug("Checksum object to object edge: to object " + uid + " " + objWH.obj.getClass().getName());
        updateInt(uid, checksum);
    }

    private static void updateNullOrBoxedPrimitivesOrString(Object obj, Checksum checksum) {
        if (obj == null) {
            updateNull(checksum);
            return;
        }
        Class<?> clz = obj.getClass();
        if (TypeUtil.isBoxedPrimitive(clz)) {
            updateBoxedPrimitive(obj, checksum);
        } else if (TypeUtil.isString(clz)) {
            updateString((String) obj, checksum);
        } else {
            throw new RuntimeException(
                    "Unexpected type: " + clz.getName());
        }
    }

    /**
     * Update the current checksum with the given {@link String}.
     * @param str the {@link String} to update the checksum with
     * @param checksum
     */
    private static void updateString(String str, Checksum checksum) {
        Log.debug("Checksum String " + str);
        for (char c : str.toCharArray()) {
            updateChar(c, checksum);
        }
    }

    /**
     * Update the current checksum with the given boxed primitive.
     * @param obj the boxed primitive to update the checksum
     *            with
     * @param checksum
     */
    private static void updateBoxedPrimitive(Object obj, Checksum checksum) {
        String className = obj.getClass().getCanonicalName();
        switch (className) {
        case "java.lang.Boolean": {
            boolean value = (Boolean) obj;
            updateBoolean(value, checksum);
            break;
        }
        case "java.lang.Byte": {
            byte value = (Byte) obj;
            updateByte(value, checksum);
            break;
        }
        case "java.lang.Character": {
            char value = (Character) obj;
            updateChar(value, checksum);
            break;
        }
        case "java.lang.Double": {
            double value = (Double) obj;
            updateDouble(value, checksum);
            break;
        }
        case "java.lang.Float": {
            float value = (Float) obj;
            updateFloat(value, checksum);
            break;
        }
        case "java.lang.Integer": {
            int value = (Integer) obj;
            updateInt(value, checksum);
            break;
        }
        case "java.lang.Long": {
            long value = (Long) obj;
            updateLong(value, checksum);
            break;
        }
        case "java.lang.Short": {
            short value = (Short) obj;
            updateShort(value, checksum);
            break;
        }
        default:
            throw new RuntimeException(
                    "Expected a boxed primitive type but found " + className);
        }
    }

    /**
     * Update the current checksum with null.
     * @param checksum
     */
    private static void updateNull(Checksum checksum) {
        Log.debug("Checksum NULL");
        updateString("null", checksum);
    }

    /**
     * Returns whether the given {@code obj} is {@code null}, or a
     * (boxed) primitive or string.
     * @param obj the object to query
     * @return true if the given {@code obj} is {@code null}, or a
     *         (boxed) primitive or string
     */
    private static boolean isNullOrBoxedPrimitiveOrString(Object obj) {
        return obj == null || TypeUtil.isImmutable(obj.getClass());
    }

    /**
     * Returns whether the given {@code obj} is one of the container
     * types: array, {@link Collection}, {@link Map} and
     * {@link Map.Entry}.
     * @param obj the object to query
     * @return true if the given {@code obj} is a container
     */
    private static boolean isContainer(Object obj) {
        return TypeUtil.isContainer(obj.getClass());
    }

    private static boolean isUnorderedContainer(Object obj) {
        return (obj instanceof Set && !(obj instanceof SortedSet))
                || (obj instanceof Map && !(obj instanceof SortedMap));
    }

    /**
     * Returns whether the given {@code obj} should be ignored, i.e.,
     * it is not a container class and is one of the ignored classes,
     * as defined in {@link TypeUtil#isIgnoredClass(Class)}.
     * @param obj the object to query
     * @return true if the given {@code obj} should be ignored
     */
    private static boolean isIgnored(Object obj) {
        Class<?> clz = obj.getClass();
        return !TypeUtil.isContainer(clz) && TypeUtil.isIgnoredClass(clz);
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
                    Log.debug("checksum static field " + field);
                    update(field.get(null));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static final class ObjectWithHash {
        private final Object obj;
        private final int hash;
        private final boolean isElementOfUnorderedContainer;
        private boolean isUnorderedContainer;
        private boolean startedChecksum;

        private ObjectWithHash(
                Object object,
                boolean isElementOfUnorderedContainer) {
            obj = object;
            hash = System.identityHashCode(obj);
            this.isElementOfUnorderedContainer = isElementOfUnorderedContainer;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ObjectWithHash that = (ObjectWithHash) o;
            return hash == that.hash;
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    private static class ChecksumStackLevel {
        private Checksum checksum;
        private long sum;
        private ChecksumStackLevel containerLevel; // null means it is outermost
        private Map<ObjectWithHash, Integer> idsByObj;

        private ChecksumStackLevel(ChecksumStackLevel containerLevel) {
            this(WrappedChecksum.newChecksum(), new HashMap<>(), containerLevel);
        }

        private ChecksumStackLevel(
                Checksum checksum,
                Map<ObjectWithHash, Integer> idsByObj,
                ChecksumStackLevel containerLevel) {
            this.checksum = checksum;
            this.idsByObj = idsByObj;
            this.containerLevel = containerLevel;
            this.sum = 0L;
        }

        private void incSum(long hash) {
            this.sum += hash;
        }

        private void updateChecksumWithSum() {
            updateLong(sum, checksum);
        }

        private long getValue() {
            return checksum.getValue();
        }
    }
}
