package jattack.compiler;

import jattack.util.TypeUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class holding bytes of classes compiled in memory.
 */
public class ClassBytes {

    private final Map<String, byte[]> bytesByClass = new HashMap<>();
    private final Set<String> packageNames = new HashSet<>();

    /**
     * Returns the bytes of the given class or null if the class does
     * not exist in this map.
     * @param className the given fully qualified class name.
     * @return the bytes of the given class.
     */
    public byte[] get(String className) {
        return bytesByClass.get(className);
    }

    public void put(String className, byte[] bytes) {
        bytesByClass.put(className, bytes);
        packageNames.add(TypeUtil.getPackageName(className));
    }

    public Map<String, byte[]> getBytesByClass() {
        return bytesByClass;
    }

    public Set<String> getPackageNames() {
        return packageNames;
    }

    public Set<String> getClassNames() {
        return bytesByClass.keySet();
    }

    public Set<Map.Entry<String, byte[]>> entrySet() {
        return bytesByClass.entrySet();
    }
}
