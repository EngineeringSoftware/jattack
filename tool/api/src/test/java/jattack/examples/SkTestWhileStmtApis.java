package jattack.examples;

import jattack.annotation.Entry;

import static jattack.Boom.*;

public class SkTestWhileStmtApis {

    private static int s = 0;

    @Entry
    public static int m() {
        int x = 1;
        whileStmt(relation("x", 10, LT), preIncIntStmt("x")).eval();
        if (x == 10 && s == 0) {
            x = intVal().eval();
        }
        s++;
        if (relation(intId("x"), intId("x"), GT).eval()) {
            // optSolverAid should work in this case to hide this hole.
            whileStmt(relation("x", 10, LT), preIncIntStmt("x")).eval();
        }
        return x;
    }
}

