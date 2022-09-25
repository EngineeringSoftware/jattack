package sketchy.ast.nodetypes;

/**
 * A terminal node.
 */
public interface TerminalNode<T> {
    String asStr();
    T getVal();
}
