/*
 * To reproduce the bug, use 11(<=11.0.12) or 16(<=16.0.2).
 *
 * $ javac C.java
 * $ java C
 * JVM will crash.
 *
 * Bug report: https://bugs.openjdk.java.net/browse/JDK-8271926
 */
import java.util.Arrays;

public class C {

    private static void m() {
        int[] arr = { 0 };
        int max = -1;
        for (int i : arr) {
            max = max;
        }
        Arrays.copyOf(arr, max);
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10_000; ++i) {
            try {
                m();
            } catch (Throwable e) {
            }
        }
    }
}
