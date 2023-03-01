package jattack.examples;

import jattack.annotation.Argument;
import jattack.annotation.Entry;
import jattack.exception.InvokedFromNotDriverException;

import static jattack.Boom.*;

public class SkTestRefIdWorksWhenVarIsNull {

    @Entry
    static void m(String a) {
        String s = "hello";
        assignStmt(refId(String.class, "s"), refId(String.class, "a")).eval();
        String t = refId(String.class, "s").eval();
        if (s != null || t != null) {
            throw new InvokedFromNotDriverException();
        }
    }

    @Argument(1)
    static String arg1() {
        return null;
    }
}
