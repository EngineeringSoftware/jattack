package jattack.data;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

/**
 * A stack of symbol table to keep track of variables' values at every
 * frame.
 */
@Deprecated
public class VarPoolStack {
    private final Deque<VarPool> stack;

    public VarPoolStack() {
        this.stack = new ArrayDeque<>();
    }

    /**
     * Push a pool onto this stack.
     */
    public void push() {
        VarPool pool = new VarPool();
        if (!isEmpty()) {
            pool.putAll(peek());
        }
        stack.push(pool);
    }

    public VarPool pop() throws NoSuchElementException {
        return stack.pop();
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    /**
     * Returns null if this stack is empty, otherwise returns the top.
     */
    public VarPool peek() {
        return stack.peek();
    }
}
