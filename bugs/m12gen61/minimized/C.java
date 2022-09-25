/*
 * To reproduce the bug, use JDK 8(<=8u261) or 11(<=11.0.8).
 *
 * $ javac C.java
 *
 * # Compilation up to level 1
 * $ java -XX:TieredStopAtLevel=1 C
 * # Output (correct)
 * 671240562
 *
 * # Compilation up to level 4 (default)
 * $ java C
 * # Output (incorrect and non-deterministic)
 * 703783828
 *
 * Bug report: https://bugs.openjdk.java.net/browse/JDK-8239244
 * CVE record: https://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2020-14792
 */
public class C {

    static int s1;

    static int s2;

    private static void m() {
        int X = 4_194_304;
        int var1 = 0;
        int i = 0;
        s1 = s1 + X;
        while (i++ < 10 && (s1 <= s2 || s1 > X)) {
            s2 = --s1 + s2;
            var1 += s1 + s2;
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100_000; ++i) {
            m();
        }
        System.out.println(s1 + s2);
    }
}
