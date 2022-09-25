package sketchy.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Class of a symbol table to keep track of variables' values.
 */
@Deprecated
public class VarPool {
    private final Map<String, Object> table;

    public VarPool() {
        this.table = new HashMap<>();
    }

    /**
     * Puts a new variable as well as its value into this pool or
     * updates the value of an existing variable in this pool.
     */
    public void put(String id, Object val) {
        table.put(id, val);
    }

    /**
     * Puts a new int array variable as well as its value into this
     * pool or updates the value of an existing variable in this pool.
     */
    public void put(String id, int[] val) {
        table.put(id, val);
    }

    /**
     * Copies all of the mappings from the given pool to this pool.
     * Since both String and int are immutable, we are safe.
     */
    public void putAll(VarPool varPool) {
        table.putAll(varPool.table);
    }

    /**
     * Returns the value for the given variable id.
     */
    public Object get(String id) {
        if (!table.containsKey(id)) {
            throw new RuntimeException("Variable NOT found in the variable pool: " + id + "!");
        }
        return table.get(id);
    }

    /**
     * Remove variable id from this pool.
     */
    public void remove(String id) {
        if (!table.containsKey(id)) {
            throw new RuntimeException("Variable NOT found in the variable pool: " + id + "!");
        }
        table.remove(id);
    }

    /**
     * Get the table of object.
     */
    public Map<String, Object> getTable() {
        return table;
    }

    @Override
    public String toString() {
        return "VarPool{" +
                "table=" + table +
                '}';
    }
}
