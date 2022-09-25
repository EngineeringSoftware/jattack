package sketchy.transformer.visitor;

import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import sketchy.data.Data;
import sketchy.transformer.visitor.NeverReachableHoleMarker;
import sketchy.util.JPUtil;

import java.util.Optional;

/**
 * Visitor to remove dead code.
 * <p>
 * Remove dead code resulting from always true or false conditions.
 * 1. If:
 *      Always true {@literal ->} Replace the if statement with the then branch,
 *      Always false:
 *        Without else branch {@literal ->} Remove the entire if statement.
 *        With else branch {@literal ->} Replace the if statement with the
 *        else branch;
 * 2. While/For:
 *      Always true {@literal ->} Replace the condition with {@code true},
 *      Always false {@literal ->} Remove the entire while statement.
 */
public class DeadCodeEliminator extends ModifierVisitor<Void> {

    @Override
    public Visitable visit(IfStmt ifStmt, Void arg) {
        Expression cond = ifStmt.getCondition();
        if (!JPUtil.isHole(cond)) {
            return super.visit(ifStmt, arg);
        }

        int holeId = JPUtil.getHoleId(cond.asMethodCallExpr());
        if (!Data.isAlwaysTrueOrFlaseCondHole(holeId)) {
            return super.visit(ifStmt, arg);
        }

        if (Data.getTrueOrFlase(holeId)) {
            // Always true, so we replace the if statement with the
            // then branch

            // Know which holes are inside the else branch and mark
            // them never reachable.
            ifStmt.getElseStmt().ifPresent(elseStmt -> {
                elseStmt.accept(new NeverReachableHoleMarker(), null);
            });

            // Return then branch only (do NOT forget to visit it)
            Statement thenStmt = ifStmt.getThenStmt();
            return thenStmt.clone().accept(this, arg);
        } else {
            // Always false, so we replace the if statement with the
            // else branch

            // Know which holes are inside the then branch and mark
            // them never reachable.
            ifStmt.getThenStmt().accept(new NeverReachableHoleMarker(), null);

            // Return else branch only (do NOT forget to visit it)
            Statement stmt = ifStmt.getElseStmt().orElse(null);
            return stmt == null ? null : stmt.clone().accept(this, arg);
        }
    }

    @Override
    public Visitable visit(WhileStmt whileStmt, Void arg) {
        Expression cond = whileStmt.getCondition();
        if (!JPUtil.isHole(cond)) {
            return super.visit(whileStmt, arg);
        }

        int holeId = JPUtil.getHoleId(cond.asMethodCallExpr());
        if (!Data.isAlwaysTrueOrFlaseCondHole(holeId)) {
            return super.visit(whileStmt, arg);
        }

        if (Data.getTrueOrFlase(holeId)) {
            // Always true, so we replace the condition with true
            WhileStmt stmt = whileStmt.setCondition(new BooleanLiteralExpr(true));
            return super.visit(stmt, arg);
        } else {
            // Always false, so we remove the entire while statement

            // Know which holes are inside the loop body and mark them
            // never reachable.
            whileStmt.getBody().accept(new NeverReachableHoleMarker(), null);

            // Remove
            return null;
        }
    }

    @Override
    public Visitable visit(ForStmt forStmt, Void arg) {
        Optional<Expression> optional = forStmt.getCompare();
        if (optional.isEmpty()) {
            return super.visit(forStmt, arg);
        }
        Expression cond = optional.get();
        if (!JPUtil.isHole(cond)) {
            return super.visit(forStmt, arg);
        }

        int holeId = JPUtil.getHoleId(cond.asMethodCallExpr());
        if (!Data.isAlwaysTrueOrFlaseCondHole(holeId)) {
            return super.visit(forStmt, arg);
        }

        if (Data.getTrueOrFlase(holeId)) {
            // Always true, so we replace the condition with true
            ForStmt stmt = forStmt.setCompare(new BooleanLiteralExpr(true));
            return super.visit(stmt, arg);
        } else {
            // Always false, so we remove the entire while statement

            // Know which holes are inside the loop body and mark them
            // never reachable.
            forStmt.getBody().accept(new NeverReachableHoleMarker(), null);

            // Remove
            return null;
        }
    }
}
