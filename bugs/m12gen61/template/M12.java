package jattack.examples;

import jattack.annotation.Entry;

import static jattack.Boom.*;

/**
 * NOTE: C67.
 */
public class M12 {

    static int s1;
    static int s2;
    static int s3;

    @Entry
    public static int m() {
        int var1 = intVal().eval() - s2;
        s2 = ~var1;
        s3 = arithmetic(intId(), intId(), SUB, ADD, MUL).eval();
        int i1 = 0;
        while (i1++ < intVal(1, 21).eval()
                && logic(relation(intId(true, "i1"), intId(true, "i1"), LE, GE, LT, GT),
                        relation(intId(true, "i1"), intId(true, "i1"), LE, GE, LT, GT),
                        OR, AND).eval()) {
            var1 *= intId(true, "i1").eval() + (--s2);
            s1 ^= arithmetic(intId(true, "i1"), intId(true, "i1"), SUB, ADD).eval();
        }
        return s1 + s2 + s3;
    }
}
