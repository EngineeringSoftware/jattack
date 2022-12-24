package jattack.examples;

import jattack.annotation.*;
import static jattack.Boom.*;

public class SkTestImValApis {

    // Only one generated program is possible.
    @Entry
    public static int m() {
       int i = asInt(10).eval() + asInt(100).eval();
       boolean b = relation(asDouble(10.0), asDouble(11.0), LT).eval();
       return i + (b ? 1 : 0);
    }
}
