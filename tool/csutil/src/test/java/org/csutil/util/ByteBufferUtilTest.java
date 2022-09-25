package org.csutil.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ByteBufferUtilTest {
    private ExtRandom r;

    @Before
    public void prepareRandom() {
        r = new ExtRandom();
    }

    @Test
    public void testTrueToByteBuffer() {
        boolean val = true;
        byte expected = 0x1;
        byte actual = ByteBufferUtil.toByteBuffer(val).get();
        Assert.assertEquals("True should be encoded as 0x1.",
                expected, actual);
    }

    @Test
    public void testFalseToByteBuffer() {
        boolean val = false;
        byte expected = 0x0;
        byte actual = ByteBufferUtil.toByteBuffer(val).get();
        Assert.assertEquals("False should be encoded as 0x0.",
                expected, actual);
    }

    @Test
    public void testByteToByteBuffer() {
        byte val = r.nextByte();
        byte actual = ByteBufferUtil.toByteBuffer(val).get();
        Assert.assertEquals("Byte must keep unchanged.",
                val, actual);
    }

    @Test
    public void testCharToByteBuffer() {
        char val = r.nextChar();
        char actual = ByteBufferUtil.toByteBuffer(val).getChar();
        Assert.assertEquals("Char must keep unchanged.",
                val, actual);
    }

    @Test
    public void testDoubleToByteBuffer() {
        double val = r.nextDouble();
        double actual = ByteBufferUtil.toByteBuffer(val).getDouble();
        assertEqualsInBits("Double must keep unchanged.",
                val, actual);
    }

    @Test
    public void testTreatAllDoubleNaNTheSameAsCanonicalOne() {
        double val = Double.longBitsToDouble(0xfff8000000000000L);
        Assert.assertTrue(Double.isNaN(val));
        double actual = ByteBufferUtil.toByteBuffer(val).getDouble();
        final double NAN = Double.NaN; // Double.longBitsToDouble(0x7ff8000000000000L)
        assertEqualsInBits("Treat all double NaN the same as the canonical one.",
                NAN, actual);
    }

    @Test
    public void testFloatToByteBuffer() {
        float val = r.nextFloat();
        float actual = ByteBufferUtil.toByteBuffer(val).getFloat();
        assertEqualsInBits("Float must keep unchanged.",
                val, actual);
    }

    @Test
    public void testTreatAllFloatNaNTheSameAsCanonicalOne() {
        float val = Float.intBitsToFloat(0xffc00000);
        Assert.assertTrue(Float.isNaN(val));
        float actual = ByteBufferUtil.toByteBuffer(val).getFloat();
        final float NAN = Float.NaN; // Float.intBitsToFloat(0x7fc00000)
        assertEqualsInBits("Treat all float NaN the same as the canonical one.",
                NAN, actual);
    }

    @Test
    public void testIntToByteBuffer() {
        int val = r.nextInt();
        int actual = ByteBufferUtil.toByteBuffer(val).getInt();
        Assert.assertEquals("Int must keep unchanged.",
                val, actual);
    }

    @Test
    public void testShortToByteBuffer() {
        short val = r.nextShort();
        short actual = ByteBufferUtil.toByteBuffer(val).getShort();
        Assert.assertEquals("Short must keep unchanged",
                val, actual);
    }

    private void assertEqualsInBits(String msg, double expected, double actual) {
        Assert.assertEquals(msg,
                Double.doubleToRawLongBits(expected),
                Double.doubleToRawLongBits(actual));
    }

    private void assertEqualsInBits(String msg, float expected, float actual) {
        Assert.assertEquals(msg,
                Float.floatToRawIntBits(expected),
                Float.floatToRawIntBits(actual));
    }
}
