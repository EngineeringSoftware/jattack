package sketchy.examples;

import static sketchy.Sketchy.*;
import sketchy.annotation.Entry;

public class SkTestVoidReturnEntryMethod {

    @Entry
    public static void m() {
        int x = intVal().eval();
    }
}
