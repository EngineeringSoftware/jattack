package jattack.ast.nodetypes;

public interface NodeWithType<T> {
    Class<T> getType();
}
