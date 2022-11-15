import jattack.annotation.Entry;
import static jattack.Boom.*;
import org.csutil.checksum.WrappedChecksum;

public class M4GEN152 {

    static int s1;

    static int s2;

    static {
    }

    public static int m() {
        int[] arr1 = { s1, s2, 1193717173, 1279218896, -417627869 };
        for (int i = 0; i < arr1.length; ++i) {
            if (((arr1[3] <= s2) || (s2 <= arr1[2]))) {
                arr1[i] &= (s1 - s2);
            }
            s1 ^= (s2 * s2);
        }
        s2 |= (s2 + arr1[3]);
        int ret = s1 + s2;
        for (int e : arr1) {
            ret += e;
        }
        return ret;
    }

    public static long main0(String[] args) {
        int N = 100000;
        if (args.length > 0) {
            N = Math.min(Integer.parseInt(args[0]), N);
        }
        WrappedChecksum cs = new WrappedChecksum();
        for (int i = 0; i < N; ++i) {
            try {
                cs.update(m());
            } catch (Throwable e) {
                if (e instanceof sketchy.exception.InvokedFromNotDriverException) {
                    throw e;
                }
                cs.update(e.getClass().getName());
            }
        }
        cs.updateStaticFieldsOfClass(M4GEN152.class);
        return cs.getValue();
    }

    public static void main(String[] args) {
        System.out.println(main0(args));
    }
}
