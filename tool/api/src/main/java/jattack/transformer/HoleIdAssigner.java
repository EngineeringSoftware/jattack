package jattack.transformer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import com.github.javaparser.printer.DefaultPrettyPrinter;
import com.github.javaparser.printer.configuration.DefaultPrinterConfiguration;
import jattack.Constants;
import jattack.data.Data;
import jattack.driver.Driver;
import jattack.transformer.visitor.HideGenericTypeArgumentPrinterVisitor;
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
    private String[] entryMethodParamTypes;
    private boolean entryMethodReturnsVoid;
    private boolean entryMethodIsStatic;

    /**
     * Flag if we mark condition hole when assigning identifier.
     */
    private final boolean markConditionHole;

    private final static DefaultPrettyPrinter printer =
            new DefaultPrettyPrinter(HideGenericTypeArgumentPrinterVisitor::new,
                                     new DefaultPrinterConfiguration());

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
        entryMethodName = entryMethod.getNameAsString();
        entryMethodParamTypes = entryMethod.getParameters().stream()
                                           .map(p -> printer.print(p.getType()) + (p.isVarArgs() ? "[]" : ""))
                                           .toArray(String[]::new);
        entryMethodReturnsVoid = entryMethod.getType().isVoidType();
        entryMethodIsStatic = entryMethod.isStatic();
    }

    public String[] getEntryMethodParamTypes() {
        return entryMethodParamTypes;
    }

    public String getEntryMethodName() {
        return entryMethodName;
    }

    public boolean getEntryMethodReturnsVoid() {
        return entryMethodReturnsVoid;
    }

    public boolean getEntryMethodIsStatic() {
        return entryMethodIsStatic;
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
