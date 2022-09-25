/*
 * To reproduce the bug, use JDK 11(<=11.0.9).
 *
 * $ javac C.java
 * $ java C
 * JVM will crash.
 *
 * Bug report: https://bugs.openjdk.java.net/browse/JDK-8258981
 */
public class C {

    static int s1;

    static int s2;

    public static void m() {
        int[] arr1 = { s1, s2, 1, 2, 0 };
        for (int i = 0; i < arr1.length; ++i) {
            if (arr1[3] <= s2 || s2 <= arr1[2]) {
                arr1[i] &= s1;
            }
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100_000; ++i) {
            m();
        }
    }
}
