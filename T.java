import jattack.annotation.Entry;
import static jattack.Boom.*;

public class T {

    static int s1;
    static int s2;

    @Entry
    public static int m() {
        int[] arr = { s1++, s2, 1, 2, intVal().eval() };
        for (int i = 0; i < arr.length; ++i) {
            if (intIdOrIntArrAccessExp().eval() <= s2
                    || relation(intId("s2"), intIdOrIntArrAccessExp(), LE).eval()) {
                arr[i] &= arithmetic(intId(), intArrAccessExp(), ADD, MUL).eval();
            }
        }
        return s1 + s2;
    }
}
