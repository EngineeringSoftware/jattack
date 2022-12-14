package jattack.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a static method that returns a value that is used as one
 * argument of the entry method. The value of the annotation
 * represents which argument, e.g., {@code @Argument(1)} means the
 * first argument.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Argument {
    int value();
}
