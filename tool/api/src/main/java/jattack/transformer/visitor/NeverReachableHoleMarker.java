package jattack.transformer.visitor;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import jattack.data.Data;
import jattack.util.JPUtil;

/**
 * Visitor to mark the holes that will never be reached.
 */
public class NeverReachableHoleMarker extends VoidVisitorAdapter<Void> {

    @Override
    public void visit(ExpressionStmt stmt, Void arg) {
        if (JPUtil.isHole(stmt)) {
            Data.addToNeverReachableHoles(JPUtil.getHoleId(stmt));
        } else {
            super.visit(stmt, arg);
        }
    }

    @Override
    public void visit(MethodCallExpr expr, Void arg) {
        if (JPUtil.isHole(expr)) {
            Data.addToNeverReachableHoles(JPUtil.getHoleId(expr));
        } else {
            super.visit(expr, arg);
        }
    }
}
