package sketchy.examples;

import sketchy.annotation.Entry;

import static sketchy.Sketchy.*;

public class SkTestAssignmentApis {

    private static int s = 0;

    @Entry
    public static int m() {
        int x = 0;
        int y = 0;
        assignStmt(intId("x"), preIncIntExp("y")).eval();
        assignStmt(intId("y"), preIncIntExp("x")).eval();
        if (relation(intId("x"), intId("x"), GT).eval()) {
            // optSolverAid should work in this case to hide this hole.
            assignStmt(intId("x"), preIncIntExp("y")).eval();
        }
        if (x == 2 && y == 2 && s == 0) {
            // allow only the first time reaching to prevent hotFilling
            // from getting into here from the second time
            return intVal().eval();
        }
        s++;
        return s;
    }
}
