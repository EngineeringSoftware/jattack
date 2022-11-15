package jattack.transformer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import jattack.Constants;
import jattack.data.Data;
import jattack.driver.Driver;
import jattack.util.JPUtil;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The transformer to assign a unique identifier for each hole.
 */
public class HoleIdAssigner extends Transformer {

    private int id;
    private final String className;
    private String entryMethodName;
    private boolean entryMethodReturnsVoid;

    /**
     * Flag if we mark condition hole when assigning identifier.
     */
    private final boolean markConditionHole;

    /**
     * Constructor.
     */
    public HoleIdAssigner(String srcPath, String className, boolean markConditionHole) {
        super(parseCuFromSource(srcPath));
        this.id = 0;
        this.className = className;
        this.markConditionHole = markConditionHole;
        checkValidity();
    }

    /**
     * Check the validity of template source code to make sure:
     *  1) the classname matches the given one;
     *  2) there exists one and only one @Entry method.
     *  TODO: the following
     *  3) entry method cannot be overloading other existing methods
     *  3) @Argument methods are all static.
     *  4) @Argument methods have no parameters.
     *  5) The number of @Argument methods matches the number of
     *     arguments of @Entry method and the annotation values
     *     increments from 1 with step 1, i.e.,
     *     @Argument(1)
     *     @Argument(2), ...
     *     @Argument(n),
     *     where n is the number of arguments of @Entry method.
     *  6) Return types of @Argument methods match types of arguments
     *     of the entry method.
     *  7) Cannot overload other existing methods.
     *  8) More checking needed to be done at runtime, in {@link
     *     Driver}.
     */
    private void checkValidity() {
        if (!origCu.getClassByName(className).isPresent()) {
            throw new RuntimeException("Class " + className +
                    " is NOT found from source!");
        }
        List<MethodDeclaration> entryMethods = origCu.findAll(
                MethodDeclaration.class, m ->
                        m.getAnnotationByName(Constants.ENTRY_ANNOT)
                                .isPresent());
        if (entryMethods.size() == 0) {
            throw new RuntimeException("NO entry method is found!");
        } else if (entryMethods.size() > 1) {
            throw new RuntimeException("More than one entry method is found!");
        }
        MethodDeclaration entryMethod = entryMethods.get(0);
        if (!entryMethod.getModifiers().contains(Modifier.staticModifier())) {
            // TODO: support non-static entry method.
            throw new RuntimeException("The entry method has to be static!");
        }
        entryMethodName = entryMethod.getNameAsString();
        entryMethodReturnsVoid = entryMethod.getType().isVoidType();
    }

    public String getEntryMethodName() {
        return entryMethodName;
    }

    public boolean getEntryMethodReturnsVoid() {
        return entryMethodReturnsVoid;
    }

    /**
     * Injects a unique identifier into each hole, starting with 1;
     * and mark condition holes if enabled.
     */
    @Override
    public void transform() {
        cu = origCu.clone();
        cu.accept(new ModifierVisitor<Void>() {
            @Override
            public Visitable visit(MethodCallExpr expr, Void arg) {
                if (JPUtil.isHole(expr)) {
                    // Assign an identifier for the hole
                    expr.setArguments(
                            new NodeList<>(new IntegerLiteralExpr(
                                    String.valueOf(++id))
                            )
                    );
                    if (markConditionHole) {
                        // Mark if this hole is a full condition
                        expr.getParentNode().ifPresent(parent -> {
                            if (parent instanceof IfStmt
                                    || parent instanceof WhileStmt
                                    || parent instanceof ForStmt) {
                                Data.addToHolesAsConditions(id);
                            }
                        });
                    }
                    return expr;
                } else {
                    return super.visit(expr, arg);
                }
            }
        }, null);
    }

    public Set<Integer> getIds() {
        Set<Integer> set = new HashSet<>();
        for (int i = 1; i <= id; i++) {
            set.add(i);
        }
        return set;
    }

    /* Utils. */

    /**
     * Parses class from source.
     */
    private static CompilationUnit parseCuFromSource(String srcPath) {
        try {
            return StaticJavaParser.parse(Paths.get(srcPath));
        } catch (IOException e) {
            throw new RuntimeException(srcPath + " NOT found!");
        }
    }
}
