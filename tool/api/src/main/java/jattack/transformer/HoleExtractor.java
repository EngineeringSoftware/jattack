package jattack.transformer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import jattack.Constants;
import jattack.util.JPUtil;

public class HoleExtractor extends Transformer {

    public static final String CLASS_NAME = "ASTCreator";

    public static final String METHOD_NAME = "createAST";

    public HoleExtractor(CompilationUnit origCu) {
        super(origCu);
        initCu();
    }

    @Override
    public void transform() {
        BlockStmt block = new BlockStmt();
        origCu.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(ExpressionStmt stmt, Void arg) {
                if (JPUtil.isHole(stmt)) {
                    int id = JPUtil.getHoleId(stmt);
                    Expression body = JPUtil.getHoleBody(stmt);
                    addHoleBodyToBlock(body, id, block);
                }
                super.visit(stmt, arg);
            }

            @Override
            public void visit(MethodCallExpr expr, Void arg) {
                if (JPUtil.isHole(expr)) {
                    int id = JPUtil.getHoleId(expr);
                    Expression body = JPUtil.getHoleBody(expr);
                    addHoleBodyToBlock(body, id, block);
                }
                super.visit(expr, arg);
            }
        }, null);
        cu.getClassByName(CLASS_NAME).get()
                .getMethodsByName(METHOD_NAME).get(0)
                .setBody(block);
    }

    private void addHoleBodyToBlock(Expression body, int id, BlockStmt block) {
        String stmt = String.format(
                "%s.%s().put(%d, %s);",
                Constants.DATA_CLZ,
                Constants.GET_AST_CACHE_METHOD,
                id,
                body);
        block.addStatement(stmt);
    }

    private void initCu() {
        cu = StaticJavaParser.parse(String.format(
                "public class %s { public static void %s(){} }",
                CLASS_NAME, METHOD_NAME));
        // keep all original imports
        for (ImportDeclaration imp : origCu.getImports()) {
            cu.addImport(imp.clone());
        }
    }
}
