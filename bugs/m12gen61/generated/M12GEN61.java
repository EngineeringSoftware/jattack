import jattack.annotation.Entry;
import static jattack.Boom.*;
import org.csutil.checksum.WrappedChecksum;

public class M12GEN61 {

    static int s1;

    static int s2;

    static int s3;

    static {
    }

    public static int m() {
        int var1 = -453613994 - s2;
        s2 = ~var1;
        s3 = (s3 - s1);
        int i1 = 0;
        while (i1++ < 20 && ((s1 > var1) || (s2 > s3))) {
            var1 *= s3 + (--s2);
            s1 ^= (var1 - s1);
        }
        return s1 + s2 + s3;
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
        cs.updateStaticFieldsOfClass(M12GEN61.class);
        return cs.getValue();
    }

    public static void main(String[] args) {
        System.out.println(main0(args));
    }
}
