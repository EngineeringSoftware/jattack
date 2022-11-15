package jattack.util;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import jattack.Constants;

import java.util.Optional;

/**
 * Utility class for JavaParser.
 */
public class JPUtil {

    public static Expression getHoleBody(ExpressionStmt stmt) {
        return getHoleBody(stmt.getExpression().asMethodCallExpr());
    }

    public static Expression getHoleBody(MethodCallExpr expr) {
        if (expr.getScope().isEmpty()) {
            throw new RuntimeException("This is not valid hole: " + expr);
        }
        return expr.getScope().get();
    }

    public static int getHoleId(ExpressionStmt stmt) {
        return getHoleId(stmt.getExpression().asMethodCallExpr());
    }

    public static int getHoleId(MethodCallExpr expr) {
        if (expr.getArguments().size() != 1
                || !expr.getArgument(0).isIntegerLiteralExpr()) {
            throw new RuntimeException("There is no identifier found in this hole: " + expr);
        }
        return expr.getArgument(0).asIntegerLiteralExpr().asNumber().intValue();
    }


    /**
     * Returns true if the given expression statement wraps
     * a method call expression matching the format
     * {@code [some api].eval(...)}.
     */
    public static boolean isHole(ExpressionStmt stmt) {
        return isHole(stmt.getExpression());
    }

    /**
     * Returns true if the given expression is a hole.
     */
    public static boolean isHole(Expression expr) {
        if (!expr.isMethodCallExpr()) {
            return false;
        }
        return isHole(expr.asMethodCallExpr());
    }

    /**
     * Returns true if the given method call expression matches the
     * format {@code [some api].eval(...)}.
     */
    public static boolean isHole(MethodCallExpr expr) {
        // TODO: this is still not 100% accurate but sufficient for
        //  now. Revisit it if we come across any issues.
        // name is "eval"
        return expr.getNameAsString().equals(Constants.EVAL_METH_NAME)
                // scope ends with one of our apis
                && expr.getScope().isPresent()
                && expr.getScope().get().isMethodCallExpr()
                && Constants.API_NAMES.contains(
                        expr.getScope().get().asMethodCallExpr().getNameAsString());
    }

    /**
     * Returns true if the given method declaration is a main method.
     */
    public static boolean isMainMethod(MethodDeclaration methodDeclaration) {
        String str = methodDeclaration.getDeclarationAsString(true, false, false);
        return str.equals("public static void main(String[])")
                || str.equals("public static void main(String...)");
    }

    /**
     * Get the main method declaration from the given class.
     */
    public static Optional<MethodDeclaration> getMain(ClassOrInterfaceDeclaration clz) {
        return clz.findFirst(MethodDeclaration.class, JPUtil::isMainMethod);
    }
}
