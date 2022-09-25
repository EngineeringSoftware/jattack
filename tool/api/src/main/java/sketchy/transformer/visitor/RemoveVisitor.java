package sketchy.transformer.visitor;

import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import sketchy.Constants;

/**
 * Remove those sketchy apis we do not want to appear in generated
 * programs, such as @Entry, @Argument and so on.
 */
public class RemoveVisitor extends ModifierVisitor<Void> {

    /**
     * Remove any @Entry annotation.
     */
    @Override
    public Visitable visit(MarkerAnnotationExpr annotation, Void arg) {
        if (isEntryAnnotation(annotation)) {
            return null;
        }
        return super.visit(annotation, arg);
    }

    /**
     * Remove any @Entry()/@Argument(value=[n]) annotation.
     */
    @Override
    public Visitable visit(NormalAnnotationExpr annotation, Void arg) {
        if (isEntryAnnotation(annotation) || isArgumentAnnotation(annotation)) {
            return null;
        }
        return super.visit(annotation, arg);
    }

    /**
     * Remove any @Argument([n]) annotation.
     */
    @Override
    public Visitable visit(SingleMemberAnnotationExpr annotation, Void arg) {
        if (isArgumentAnnotation(annotation)) {
            return null;
        }
        return super.visit(annotation, arg);
    }

    /**
     * Returns ture if the given annotation is @Entry.
     */
    public static boolean isEntryAnnotation(AnnotationExpr annotation) {
        String name = annotation.getNameAsString();
        return name.equals(Constants.ENTRY_ANNOT)
                || name.equals(Constants.ENTRY_ANNOT_FULL_NAME);
    }

    /**
     * Returns ture if the given annotation is @Argument.
     */
    public static boolean isArgumentAnnotation(AnnotationExpr annotation) {
        String name = annotation.getNameAsString();
        return name.equals(Constants.ARGUMENT_ANNOT)
                || name.equals(Constants.ARGUMENT_ANNOT_FULL_NAME);
    }
}
