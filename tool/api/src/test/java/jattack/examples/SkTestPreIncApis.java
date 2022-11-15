package jattack.examples;

import jattack.annotation.Entry;
import static jattack.Boom.*;

public class SkTestPreIncApis {

    private static int s = 0;

    @Entry
    public static int m() {
        int x = 0;
        int y = 0;
        preIncIntStmt("s").eval();
        if (relation(intId("x"), intId("x"), GT).eval()) {
            preIncIntStmt().eval();
        }
        return x + y;
    }
}
