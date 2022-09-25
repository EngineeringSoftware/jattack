package sketchy.examples;

import sketchy.annotation.Argument;
import sketchy.annotation.Entry;

import static sketchy.Sketchy.*;

public class SkTestBoolIdApis {

    private static boolean bs = false;

    @Entry
    public static int m(boolean b, int i) {
        int x = intId().eval();
        if (logic(boolId(), boolId("bs")).eval()) {
            x += 1;
        }
        return x;
    }

    @Argument(1)
    public static boolean arg1() {
        return true;
    }

    @Argument(2)
    public static int arg2() {
        return 99;
    }
}
