package jattack.transformer.visitor;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import jattack.Constants;
import jattack.data.Data;
import jattack.log.Log;
import jattack.util.JPUtil;

/**
 * Visitor to fill holes represented by jattack APIs with plain Java
 * expressions.
 */
public class HoleFiller extends ModifierVisitor<Void> {

    private final boolean trackHoles;

    public HoleFiller() {
        this(false);
    }

    public HoleFiller(boolean trackHoles) {
        this.trackHoles = trackHoles;
    }

    /**
     * Transform any hole marked with eval().
     */
    @Override
    public Visitable visit(ExpressionStmt stmt, Void arg) {
        if (JPUtil.isHole(stmt)) {
            return transformStmt(stmt);
        } else {
            return super.visit(stmt, arg);
        }
    }

    /**
     * Transform any hole marked with eval().
     */
    @Override
    public Visitable visit(MethodCallExpr expr, Void arg) {
        if (JPUtil.isHole(expr)) {
            return transformExpr(expr);
        } else {
            return super.visit(expr, arg);
        }
    }

    private Visitable transformStmt(ExpressionStmt hole) {
        // Suppose this hole looks like "<exp>.eval(<int>>);"

        int holeId = JPUtil.getHoleId(hole);
        if (!Data.isTheHoleFilled(holeId)) {
            // TODO: Maybe one day we change our mind and decide to
            //  remove those holes unreachable since they might have
            //  impact on JIT even though they will never be executed.
            //  For now we leave them there so we import packages
            //  so we can pass type check.
            return hole;
        }
        String stmt = Data.getJavaStrOfHole(holeId);
        // TODO: We do not wrap statements to collect trackHoles info
        //  because I don't think the wrapping works for statements.
        //  For now it is ok since we do not have any serious
        //  templates with statement holes. All statement holes are
        //  used in the templates for testing only.
        return StaticJavaParser.parseStatement(stmt);
    }

    private Visitable transformExpr(MethodCallExpr hole) {
        // Suppose this hole looks like "<exp>.eval(<int>>)"

        int holeId = JPUtil.getHoleId(hole);
        if (!Data.isTheHoleFilled(holeId)) {
            // TODO: Maybe one day we change our mind and decide to
            //  remove those holes unreachable since they might have
            //  impact on JIT even though they will never be executed.
            //  For now we leave them there so we import packages
            //  so we can pass type check.
            return hole;
        }
        String expr = Data.getJavaStrOfHole(holeId);
        try {
            Expression filledHole = StaticJavaParser.parseExpression(expr);
            return trackHoles ? wrapHoleFilling(filledHole, holeId) : filledHole;
        } catch (ParseProblemException e) {
            throw new RuntimeException("Expect an expression for this hole but encountered " + expr);
        }
    }

    // Wrap with track method, so we can know if this hole will be
    // covered or not when the generated program is run.
    private MethodCallExpr wrapHoleFilling(Expression filledHole, int holeId) {
        return new MethodCallExpr(Constants.TRACK_METHOD,
                filledHole, new IntegerLiteralExpr(String.valueOf(holeId)));
    }
}
