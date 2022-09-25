/*
 * To reproduce the bug, use JDK 16(<=16.0.2).
 *
 * $ javac C.java
 * $ java C
 * JVM will crash.
 *
 * Bug report: https://bugs.openjdk.java.net/browse/JDK-8271276
 */
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class C {

    public static void m(String s) {
        Pattern pattern = Pattern.compile("");
        Matcher matcher = pattern.matcher(s);
    }
    public static void main(String[] args) {
        for (int i = 0; i < 10_000; ++i) {
            try {
                m(null);
            } catch (Throwable e) {
            }
        }
    }
}
