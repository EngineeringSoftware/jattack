package jattack.data;

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

    private Map<String, RuntimeSymbol> table;

    public Memory() {
        table = new HashMap<>();
    }

    public Map<String, RuntimeSymbol> getTable() {
        return table;
    }

    public boolean containsKey(String name) {
        return table.containsKey(name);
    }

    public void put(String name, String desc, Object val) {
        if (!TypeUtil.isTypeDescSupported(desc)) {
            return;
        }
        if (TypeUtil.isDescPrimitive(desc)) {
            // as we allow only boxed types inside
            desc = TypeUtil.primitiveDescToBoxedDesc(desc);
        }
        table.put(name, new RuntimeSymbol(name, desc, val)); // No! We do not want a deep copy
    }

    public void updateValue(String name, Object val) {
        if (!table.containsKey(name)) {
            throw new NoSuchElementException(
                    "Symbol " + name + " does not exist in memory!");
        }
        table.get(name).setValue(val);
    }

    /**
     * Returns the runtiem value of the given symbol name.
     * @param name the given symbol name
     * @return the correspoinding runtime value
     * @throws NoSuchElementException if the given symbol does not
     * exist in memory
     */
    public Object getValue(String name) {
        if (!table.containsKey(name)) {
            throw new NoSuchElementException(
                    "Symbol " + name + " does not exist in memory!");
        }
        return table.get(name).getValue();
    }

    public void reset() {
        table.clear();
    }
}
