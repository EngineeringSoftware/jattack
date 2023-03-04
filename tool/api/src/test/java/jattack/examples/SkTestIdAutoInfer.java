package jattack.examples;

import jattack.annotation.Entry;

import static jattack.Boom.*;

public class SkTestIdAutoInfer {

    static class A {}
    static class B extends A {}

    @Entry
    static void m() {
        useDeclaringTypeToCheckAssignable();
        useCast();
        nullAssignableToCompatibleType();
        nullNotAssignableToIncompatibleType();
    }

    static void useDeclaringTypeToCheckAssignable() {
        A b1 = new B();
        B b2 = new B();
        // only one possibility, which is b2
        B b = refId(B.class).eval();
    }

    static void useCast() {
        A b1 = new B();
        B b = (B) refId(A.class).eval();
    }

    static void useCastAPI() {
        A b1 = new B();
        B b = cast(B.class, refId(A.class)).eval();
    }

    static void nullAssignableToCompatibleType() {
        B b1 = null;
        A a = refId(A.class).eval();
    }

    static void nullNotAssignableToIncompatibleType() {
        A a1 = null;
        B b1 = null;
        // only one possibility, which is b1
        B b = refId(B.class).eval();
    }
}
