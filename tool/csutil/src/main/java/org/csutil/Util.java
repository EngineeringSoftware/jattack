package org.csutil;

import org.csutil.checksum.WrappedChecksum;

/**
 * Util class to make a checksum.
 */
public class Util {

    /* Hash all static fields of the given class and result. */
    public static long hash(Class<?> clz, int result) {
        WrappedChecksum checksum = new WrappedChecksum();
        checksum.updateStaticFieldsOfClass(clz);
        checksum.update(result);
        return checksum.getValue();
    }

    /* Hash all static fields of the given class and result. */
    public static long hash(Class<?> clz, long result) {
        WrappedChecksum checksum = new WrappedChecksum();
        checksum.updateStaticFieldsOfClass(clz);
        checksum.update(result);
        return checksum.getValue();
    }

    /* Hash all static fields of the given class and result. */
    public static long hash(Class<?> clz, double result) {
        WrappedChecksum checksum = new WrappedChecksum();
        checksum.updateStaticFieldsOfClass(clz);
        checksum.update(result);
        return checksum.getValue();
    }

    public static long hash(String str) {
        WrappedChecksum checksum = new WrappedChecksum();
        checksum.update(str);
        return checksum.getValue();
    }
}
