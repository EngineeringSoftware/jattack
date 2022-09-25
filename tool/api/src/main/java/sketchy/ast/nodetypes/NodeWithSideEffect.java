package sketchy.ast.nodetypes;

public interface NodeWithSideEffect<T> {
    void updateVal(T val);
}
