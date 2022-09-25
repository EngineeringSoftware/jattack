/*
 * To reproduce the bug, use JDK 8(<=8u311) or 11(<=11.0.13) or
 * 17(<=17.0.1).
 *
 * $ javac C.java
 * $ java -Xmx7g C
 * JVM will crash.
 *
 * Bug report: https://bugs.openjdk.java.net/browse/JDK-8271130
 * CVE record: https://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2022-21305
 */
public class C {

    public static void m() {
        int X = 536_870_908;
        int[] a = new int[X + 1]; // Object[] also works
        a[X] = 1;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 1_000; ++i) {
            m();
        }
    }
}
