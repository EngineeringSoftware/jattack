package jattack.examples;

import jattack.annotation.Entry;

import static jattack.Boom.*;

public class SkTestCastApi {

    @Entry
    static void m() {
        testCastToByte();
        testCastToShort();
        testCastToChar();
        testCastToInt();
        testCastToLong();
        testCastToFloat();
        testCastToDouble();
        testCastObject();
    }

    static void testCastToByte() {
        // Cast to short
        int a = cast(Byte.class, shortVal()).eval();
        int b = cast(Byte.class, charVal()).eval();
        int c = cast(Byte.class, intVal()).eval();
        int d = cast(Byte.class, longVal()).eval();
        long e = cast(Byte.class, floatVal()).eval();
        long f = cast(Byte.class, doubleVal()).eval();
    }

    static void testCastToShort() {
        // Cast to short
        int a = cast(Short.class, byteVal()).eval();
        int b = cast(Short.class, charVal()).eval();
        int c = cast(Short.class, intVal()).eval();
        int d = cast(Short.class, longVal()).eval();
        long e = cast(Short.class, floatVal()).eval();
        long f = cast(Short.class, doubleVal()).eval();
    }

    static void testCastToChar() {
        // Cast to character
        int a = cast(Character.class, byteVal()).eval();
        int b = cast(Character.class, shortVal()).eval();
        int c = cast(Character.class, intVal()).eval();
        int d = cast(Character.class, longVal()).eval();
        long e = cast(Character.class, floatVal()).eval();
        long f = cast(Character.class, doubleVal()).eval();
    }

    static void testCastToInt() {
        // Cast to int
        int a = arithmetic(cast(Integer.class, byteVal()), intVal()).eval();
        int b = arithmetic(cast(Integer.class, shortVal()), intVal()).eval();
        int c = arithmetic(cast(Integer.class, charVal()), intVal()).eval();
        long d = cast(Integer.class, longVal()).eval();
        long e = cast(Integer.class, floatVal()).eval();
        long f = cast(Integer.class, doubleVal()).eval();
        if (relation(cast(Integer.class, byteVal()), intVal()).eval()) {
            int x = intVal().eval();
        }
        if (relation(cast(Integer.class, shortVal()), intVal()).eval()) {
            int x = intVal().eval();
        }
        if (relation(cast(Integer.class, charVal()), intVal()).eval()) {
            int x = intVal().eval();
        }
    }

    static void testCastToLong() {
        // Cast to long
        long a = arithmetic(cast(Long.class, byteVal()), longVal()).eval();
        long b = arithmetic(cast(Long.class, shortVal()), longVal()).eval();
        long c = arithmetic(cast(Long.class, charVal()), longVal()).eval();
        long d = arithmetic(cast(Long.class, intVal()), longVal()).eval();
        long e = cast(Long.class, floatVal()).eval();
        long f = cast(Long.class, doubleVal()).eval();
        if (relation(cast(Long.class, byteVal()), longVal()).eval()) {
            int x = intVal().eval();
        }
        if (relation(cast(Long.class, shortVal()), longVal()).eval()) {
            int x = intVal().eval();
        }
        if (relation(cast(Long.class, charVal()), longVal()).eval()) {
            int x = intVal().eval();
        }
        if (relation(cast(Long.class, intVal()), longVal()).eval()) {
            int x = intVal().eval();
        }
    }

    static void testCastToFloat() {
        // Cast to float
        float a = arithmetic(cast(Float.class, byteVal()), floatVal()).eval();
        float b = arithmetic(cast(Float.class, shortVal()), floatVal()).eval();
        float c = arithmetic(cast(Float.class, charVal()), floatVal()).eval();
        float d = arithmetic(cast(Float.class, intVal()), floatVal()).eval();
        float e = arithmetic(cast(Float.class, longVal()), floatVal()).eval();
        float f = cast(Float.class, doubleVal()).eval();
        if (relation(cast(Float.class, byteVal()), floatVal()).eval()) {
            int x = intVal().eval();
        }
        if (relation(cast(Float.class, shortVal()), floatVal()).eval()) {
            int x = intVal().eval();
        }
        if (relation(cast(Float.class, charVal()), floatVal()).eval()) {
            int x = intVal().eval();
        }
        if (relation(cast(Float.class, intVal()), floatVal()).eval()) {
            int x = intVal().eval();
        }
        if (relation(cast(Float.class, longVal()), floatVal()).eval()) {
            int x = intVal().eval();
        }
    }

    static void testCastToDouble() {
        // Cast to double
        double a = arithmetic(cast(Double.class, byteVal()), doubleVal()).eval();
        double b = arithmetic(cast(Double.class, shortVal()), doubleVal()).eval();
        double c = arithmetic(cast(Double.class, charVal()), doubleVal()).eval();
        double d = arithmetic(cast(Double.class, intVal()), doubleVal()).eval();
        double e = arithmetic(cast(Double.class, longVal()), doubleVal()).eval();
        double f = arithmetic(cast(Double.class, floatVal()), doubleVal()).eval();
        if (relation(cast(Double.class, byteVal()), doubleVal()).eval()) {
            int x = intVal().eval();
        }
        if (relation(cast(Double.class, shortVal()), doubleVal()).eval()) {
            int x = intVal().eval();
        }
        if (relation(cast(Double.class, charVal()), doubleVal()).eval()) {
            int x = intVal().eval();
        }
        if (relation(cast(Double.class, intVal()), doubleVal()).eval()) {
            int x = intVal().eval();
        }
        if (relation(cast(Double.class, longVal()), doubleVal()).eval()) {
            int x = intVal().eval();
        }
        if (relation(cast(Double.class, floatVal()), doubleVal()).eval()) {
            int x = intVal().eval();
        }
    }

    static void testCastObject() {
        Object o = "hello";
        String s = cast(String.class, refId(Object.class)).eval();
    }
}
