package sketchy.examples;

import sketchy.annotation.Entry;

import static sketchy.Sketchy.*;

public class SkTestShiftApis {

    private static int s1;

    @Entry
    public static int m() {
        int x = 1;
        int y = 10;
        int z = arithOrShift(intId(), intId()).eval();
        z += shift(intId(), intId()).eval();
        return x + y + z;
    }
}
