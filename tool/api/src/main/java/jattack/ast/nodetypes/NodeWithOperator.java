package jattack.ast.nodetypes;

import jattack.ast.operator.Op;

public interface NodeWithOperator<O extends Op> {
    O getOp();
}
