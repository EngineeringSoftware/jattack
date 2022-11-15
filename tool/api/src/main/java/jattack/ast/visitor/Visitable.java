package jattack.ast.visitor;

/**
 * Interface for all visitable classes.
 */
public interface Visitable {
    void accept(Visitor v);
}
