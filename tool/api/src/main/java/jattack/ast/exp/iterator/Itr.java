package jattack.ast.exp.iterator;

/**
 * Interface implemented by all iterators.
 */
public interface Itr {

    void next();

    void reset();

    boolean hasNext();

    boolean isReset();
}
