package jattack.examples;

import jattack.annotation.*;

import static jattack.Boom.*;

public class SkTestEscapingSpecialChars {

    @Entry
    static void m() {
        char c1 = asChar('\t').eval();
        char c2 = asChar('\b').eval();
        char c3 = asChar('\n').eval();
        char c4 = asChar('\r').eval();
        char c5 = asChar('\f').eval();
        char c6 = asChar('\'').eval();
        char c7 = asChar('\"').eval();
        char c8 = asChar('\\').eval();
    }
}
