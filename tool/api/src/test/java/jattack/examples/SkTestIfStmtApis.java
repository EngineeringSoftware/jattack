package jattack.examples;

import jattack.annotation.Entry;

import static jattack.Boom.*;

public class SkTestIfStmtApis {

    private static int s = 0;

    @Entry
    public static int m() {
        int x = 10;
        int y = x - 1;
        ifStmt(relation("x", "y", LT), preIncIntStmt("x"), preIncIntStmt("y")).eval();
        if (x == y && s == 0) {
            x = intVal().eval();
        }
        s++;
        if (relation(intId("x"), intId("x"), GT).eval()) {
            ifStmt(relation("x", "y", LT), preIncIntStmt("x"), preIncIntStmt("y")).eval();
        }
        return x + y;
    }
}
