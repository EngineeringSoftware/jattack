package jattack.examples;

import jattack.annotation.Arguments;
import jattack.annotation.Entry;

import java.util.List;

import static jattack.Boom.*;

public class SkTestArgumentWithGenericTypeUsingArgumentsAnnotation {

    @Entry
    public static <T> void m(List<List<T>> s) {
        int x = intVal().eval();
    }

    @Arguments()
    public static Object[] args() {
        return new Object[]{ List.of(List.of("hello")) };
    }
}
