package sketchy.ast.nodetypes;

import sketchy.ast.operator.Op;

public interface NodeWithOperator<O extends Op> {
    O getOp();
}
