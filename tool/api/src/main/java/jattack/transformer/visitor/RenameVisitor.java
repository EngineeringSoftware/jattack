package jattack.transformer.visitor;

import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;

/**
 * Rename every usage of the template class.
 */
public class RenameVisitor extends ModifierVisitor<Void> {

    private final String inClzName;
    private final String outClzName;

    public RenameVisitor(String inClzName, String outClzName) {
        this.inClzName = inClzName;
        this.outClzName = outClzName;
    }

    @Override
    public Visitable visit(SimpleName name, Void arg) {
        if (name.getIdentifier().equals(inClzName)) {
            name.setIdentifier(outClzName);
        }
        return super.visit(name, arg);
    }
}
