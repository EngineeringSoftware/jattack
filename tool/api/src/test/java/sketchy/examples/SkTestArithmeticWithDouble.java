package sketchy.examples;

import sketchy.annotation.Entry;
import static sketchy.Sketchy.*;

public class SkTestArithmeticWithDouble {

    private static int s1 = 0;

    @Entry
    public static double m() {
        double a = arithmetic(doubleVal(), doubleVal(), ADD).eval();
        return a + 1.0;
    }
}
