package sketchy.ast.nodetypes;

public interface NodeWithType<T> {
    Class<T> getType();
}
