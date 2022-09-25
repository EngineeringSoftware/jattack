package org.csutil.util;

import java.nio.ByteBuffer;

/**
 * Convert a primitive type to ByteBuffer.
 */
public class ByteBufferUtil {

    public static ByteBuffer toByteBuffer(boolean val) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1);
        byte byteVal = (byte) (val ? 0x1 : 0x0);
        byteBuffer.put(byteVal);
        byteBuffer.rewind();
        return byteBuffer;
    }

    public static ByteBuffer toByteBuffer(byte val) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1);
        byteBuffer.put(val);
        byteBuffer.rewind();
        return byteBuffer;
    }

    public static ByteBuffer toByteBuffer(char val) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(2);
        byteBuffer.putChar(val);
        byteBuffer.rewind();
        return byteBuffer;
    }

    public static ByteBuffer toByteBuffer(double val) {
        // Treat all NaN the same as the canonical one
        if (Double.isNaN(val)) {
            val = Double.NaN;
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.putDouble(val);
        byteBuffer.rewind();
        return byteBuffer;
    }

    public static ByteBuffer toByteBuffer(float val) {
        // Treat all NaN the same as the canonical one
        if (Double.isNaN(val)) {
            val = Float.NaN;
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.putFloat(val);
        byteBuffer.rewind();
        return byteBuffer;
    }

    public static ByteBuffer toByteBuffer(int val) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.putInt(val);
        byteBuffer.rewind();
        return byteBuffer;
    }

    public static ByteBuffer toByteBuffer(long val) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.putLong(val);
        byteBuffer.rewind();
        return byteBuffer;
    }

    public static ByteBuffer toByteBuffer(short val) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(2);
        byteBuffer.putShort(val);
        byteBuffer.rewind();
        return byteBuffer;
    }
}
