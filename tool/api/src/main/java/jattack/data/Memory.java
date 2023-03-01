package jattack.data;

import org.objectweb.asm.Type;

import jattack.util.TypeUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Class to track runtime values of symbols (local variables or
 * fields).
 */
public class Memory {

    public static final class Null {
        // Singleton
        // forbid from being constructed from outside
        private Null() {}

        @Override
        public boolean equals(Object other) {
            return other instanceof Null;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(null);
        }
    }

    public static final Null NULL = new Null();

    private Map<String, Object> table;

    public Memory() {
        reset();
    }

    public Map<String, Object> getTable() {
        return table;
    }

    public boolean containsKey(String name) {
        return table.containsKey(name);
    }

    public void put(String name, Object val) {
        if (val != null) {
            String desc = Type.getDescriptor(val.getClass());
            if (!TypeUtil.isTypeDescSupported(desc)) {
                return;
            }
        }
        table.put(name, val); // No! We do not want a deep copy
    }

    /**
     * Returns the runtiem value of the given symbol name.
     * @param name the given symbol name
     * @return the correspoinding runtime value
     * @throws NoSuchElementException if the given symbol does not
     * exist in memory
     */
    public Object get(String name) {
        if (!table.containsKey(name)) {
            throw new NoSuchElementException(
                    "Symbol " + name + " does not exist in memory!");
        }
        return table.get(name);
    }

    public void reset() {
        table = new HashMap<>();
    }
}
