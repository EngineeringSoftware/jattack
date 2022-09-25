package sketchy.examples;

import sketchy.annotation.Entry;

import static sketchy.Sketchy.*;

public class SkTestBlockStmtApis {

    private static int s = 0;

    @Entry
    public static void m() {
        testEmptyBlock();
        testSimpleBlock();
        testBlockAsBodyOfWhile();
        testSolverAidWorksForBlock();
        s++;
    }

    private static void testEmptyBlock() {
        block().eval();
    }

    private static void testSimpleBlock() {
        int x = 0;
        int y = 0;
        block(preIncIntStmt("x"), preIncIntStmt("y")).eval();
        if (x == y && s == 0) {
            y = x + intVal().eval();
        }
    }

    private static void testBlockAsBodyOfWhile() {
        int x = 1;
        int y = 2;
        whileStmt(relation(intId("x"), intId("y"), LT),
                block(
                        ifStmt(relation(arithmetic("x", 1, ADD), "y", GE),
                                block(preIncIntStmt("x"))),
                        assignStmt(intId("s"), intId()))).eval();
        if (x == y && s == 0) {
            y = x + intVal().eval();
        }
    }

    private static void testSolverAidWorksForBlock() {
        int x = 1;
        if (relation(intId("x"), intId("x"), GT).eval()) {
            // optSolverAid should work in this case to hide this hole.
            block(preIncIntStmt("x")).eval();
        }
    }
}
