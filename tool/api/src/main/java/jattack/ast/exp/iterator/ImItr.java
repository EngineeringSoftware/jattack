package jattack.ast.exp.iterator;

/**
 * Iterator for an immutable AST node that contains only one value
 * never changed.
 */
public class ImItr extends ExpItr {

    @Override
    public boolean hasNext() {
        return isReset();
    }
}
