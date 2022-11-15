package jattack.ast.nodetypes;

public interface NodeWithSideEffect<T> {
    void updateVal(T val);
}
