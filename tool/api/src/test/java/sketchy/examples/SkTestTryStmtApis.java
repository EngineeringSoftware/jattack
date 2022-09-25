package sketchy.examples;

import sketchy.annotation.Entry;

import static sketchy.Sketchy.*;

public class SkTestTryStmtApis {

    private static int s = 0;

    @Entry
    public static void m() {
        s++;
        testEmptyBlocks();
        testSimpleTryStmt();
        testTryInBodyOfWhile();
        testSolverAidWorksForBlock();
    }

    private static void testSolverAidWorksForBlock() {
        int x = 1;
        if (relation(intId("x"), intId("x"), GT).eval()) {
            // optSolverAid should work in this case to hide this hole.
            tryStmt(block(assignStmt(intId("x"), arithmetic("x", 0, DIV))),
                    ArithmeticException.class,
                    block(preIncIntStmt("x")),
                    block(preIncIntStmt("x"))).eval();
        }
    }

    private static void testTryInBodyOfWhile() {
        int x = 1;
        whileStmt(relation(intId("x"), 2, LE),
                tryStmt(block(assignStmt(intId("x"), arithmetic("x", 0, DIV))),
                        ArithmeticException.class,
                        block(preIncIntStmt("x")),
                        block(preIncIntStmt("x")))).eval();
        if (x == 3 && s == 1) {
            x += intVal().eval();
        }
    }

    private static void testSimpleTryStmt() {
        int x = 10;
        tryStmt(block(assignStmt(intId("x"), arithmetic("x", 0, DIV))),
                ArithmeticException.class,
                block(assignStmt(intId("x"), asInt(-1))),
                block()).eval();
        if (x == -1 && s == 1) {
            x = x + intVal().eval();
        }
    }

    private static void testEmptyBlocks() {
        tryStmt(block(), Exception.class, block(), block()).eval();
    }
}
