package sketchy.ast.visitor;

import sketchy.ast.exp.AssignExp;
import sketchy.ast.exp.BAriExp;
import sketchy.ast.exp.LongVal;
import sketchy.ast.exp.PreIncExp;
import sketchy.ast.exp.RefId;
import sketchy.ast.exp.ShiftExp;
import sketchy.ast.exp.BoolId;
import sketchy.ast.exp.BoolVal;
import sketchy.ast.exp.DoubleId;
import sketchy.ast.exp.DoubleVal;
import sketchy.ast.exp.ImBoolVal;
import sketchy.ast.exp.ImDoubleVal;
import sketchy.ast.exp.ImIntVal;
import sketchy.ast.exp.IntArrVal;
import sketchy.ast.exp.IntId;
import sketchy.ast.exp.IntVal;
import sketchy.ast.exp.LogExp;
import sketchy.ast.exp.RefArrAccessExp;
import sketchy.ast.exp.RelExp;
import sketchy.ast.nodetypes.TerminalNode;
import sketchy.ast.stmt.BlockStmt;
import sketchy.ast.stmt.ExprStmt;
import sketchy.ast.stmt.IfStmt;
import sketchy.ast.stmt.Stmt;
import sketchy.ast.stmt.TryStmt;
import sketchy.ast.stmt.WhileStmt;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Visitor to print a AST node.
 */
public class PrintVisitor extends Visitor {

    private final Deque<String> stack = new ArrayDeque<>();

    public String getResult() {
        if (stack.isEmpty()) {
            throw new RuntimeException("PrinterVisitor stack is empty!");
        }
        return stack.peek();
    }

    @Override
    public <N extends Number> void endVisit(BAriExp<N> node) {
        String right = stack.pop();
        String left = stack.pop();
        stack.push(String.format("(%s %s %s)",
                left, node.getOp().asStr(), right));
    }

    @Override
    public <N extends Number> void endVisit(ShiftExp<N> node) {
        String right = stack.pop();
        String left = stack.pop();
        stack.push(String.format("(%s %s %s)",
                left, node.getOp().asStr(), right));
    }

    @Override
    public void endVisit(BoolId node) {
        endVisitTerminalNode(node);
    }

    @Override
    public void endVisit(BoolVal node) {
        endVisitTerminalNode(node);
    }

    @Override
    public void endVisit(ImBoolVal node) {
        endVisitTerminalNode(node);
    }

    @Override
    public void endVisit(IntVal node) {
        endVisitTerminalNode(node);
    }

    @Override
    public void endVisit(ImIntVal node) {
        endVisitTerminalNode(node);
    }

    @Override
    public void endVisit(IntId node) {
        endVisitTerminalNode(node);
    }

    @Override
    public void endVisit(DoubleId node) {
        endVisitTerminalNode(node);
    }

    @Override
    public void endVisit(DoubleVal node) {
        endVisitTerminalNode(node);
    }

    @Override
    public void endVisit(ImDoubleVal node) {
        endVisitTerminalNode(node);
    }

    @Override
    public void endVisit(LongVal node) {
        endVisitTerminalNode(node);
    }

    @Override
    public void endVisit(RefId<?> node) {
        endVisitTerminalNode(node);
    }

    @Override
    public void endVisit(IntArrVal node) {
        endVisitTerminalNode(node);
    }

    @Override
    public void endVisit(RefArrAccessExp<?, ?> node) {
        String id = stack.pop();
        String index = stack.pop();
        stack.push(id + "[" + index + "]");
    }

    @Override
    public void endVisit(LogExp node) {
        String right = stack.pop();
        String left = stack.pop();
        stack.push(String.format("(%s %s %s)",
                left, node.getOp().asStr(), right));
    }

    @Override
    public <N extends Number> void endVisit(RelExp<N> node) {
        String right = stack.pop();
        String left = stack.pop();
        stack.push(String.format("(%s %s %s)",
                left, node.getOp().asStr(), right));
    }

    @Override
    public <N extends Number> void endVisit(PreIncExp<N> node) {
        String operand = stack.pop();
        stack.push(String.format("(++%s)", operand));
    }

    @Override
    public <T> void endVisit(AssignExp<T> node) {
        String target = stack.pop();
        String value = stack.pop();
        stack.push(String.format("(%s = %s)", target, value));
    }

    @Override
    public void visitStmt(ExprStmt node) {
        StringBuilder sb = new StringBuilder();
        node.getExpression().accept(this);
        sb.append(stripParentheses(stack.pop())).append(";");
        stack.push(sb.toString());
    }

    @Override
    public void visitStmt(IfStmt node) {
        StringBuilder sb = new StringBuilder();
        node.getCondition().accept(this);
        sb.append("if (").append(stripParentheses(stack.pop())).append(")");
        node.getThenStmt().accept(this);
        sb.append(stack.pop());
        if (node.hasElseBranch()) {
            sb.append("else ");
            node.getElseStmt().accept(this);
            sb.append(stack.pop());
        }
        stack.push(sb.toString());
    }

    @Override
    public void visitStmt(WhileStmt node) {
        StringBuilder sb = new StringBuilder();
        node.getCondition().accept(this);
        sb.append("while (").append(stripParentheses(stack.pop())).append(")");
        node.getBody().accept(this);
        sb.append(stack.pop());
        stack.push(sb.toString());
    }

    @Override
    public void visitStmt(BlockStmt node) {
        StringBuilder sb = new StringBuilder("{");
        for (Stmt s : node.getStmts()) {
            s.accept(this);
            sb.append(stack.pop());
        }
        sb.append("}");
        stack.push(sb.toString());
    }

    @Override
    public <T extends Throwable> void visitStmt(TryStmt<T> node) {
        StringBuilder sb = new StringBuilder();
        sb.append("try");
        node.getTryBlock().accept(this);
        sb.append(stack.pop());
        sb.append("catch (")
                .append(node.getExceptionType().getCanonicalName())
                .append(" e")
                .append(")");
        node.getCatchBlock().accept(this);
        sb.append(stack.pop());
        if (node.hasFinally()) {
            sb.append("finally");
            node.getFinallyBlock().accept(this);
            sb.append(stack.pop());
        }
        stack.push(sb.toString());
    }

    private void endVisitTerminalNode(TerminalNode<?> node) {
        stack.push(node.asStr());
    }

    private static String stripParentheses(String exp) {
        if (surroundedWithParentheses(exp)) {
            exp = exp.substring(1, exp.length() - 1);
        }
        return exp;
    }

    private static boolean surroundedWithParentheses(String exp) {
        return exp.startsWith("(") && exp.endsWith(")");
    }
}
