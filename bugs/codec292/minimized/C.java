/*
 * To reproduce the bug, use JDK 8(<=8u341) or 11(<=11.0.13) or
 * 17(<=17.0.1).
 *
 * $ javac C.java
 *
 * # Compilation up to level 4 (default)
 * $ java C
 * # Output (incorrect and non-deterministic)
 * 5597
 *
 * # Compilation up to level 1
 * $ java -XX:TieredStopAtLevel=1 C
 * # Output (correct)
 * 10000
 *
 * Bug report: https://bugs.openjdk.java.net/browse/JDK-8271459
 */
public class C {

    static String m() {
        StringBuilder sb = new StringBuilder(-1);
        return sb.toString();
    }

    public static void main(String[] args) {
        int sum = 0;
        for (int i = 0; i < 10_000; ++i) {
            try {
                m();
            } catch (Throwable e) {
                sum += 1;
            }
        }
        System.out.println(sum); // should be 10_000
    }
}
