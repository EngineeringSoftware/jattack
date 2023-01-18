package jattack.examples;

import jattack.annotation.Argument;
import jattack.annotation.Entry;

import java.util.List;

import static jattack.Boom.*;

public class SkTestArgumentWithGenericTypeUsingArgumentAnnotation {

    @Entry
    public static <T> void m(List<List<T>> s) {
        int x = intVal().eval();
    }

    @Argument(1)
    public static List<List<String>> arg1() {
        return List.of(List.of("hello"));
    }
}
