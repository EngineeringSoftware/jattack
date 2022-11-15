package org.apache.commons.math4.complex;

import org.apache.commons.math4.exception.MathIllegalArgumentException;
import org.apache.commons.math4.exception.OutOfRangeException;
import org.apache.commons.math4.exception.util.LocalizedFormats;
import org.apache.commons.math4.util.FastMath;
import org.apache.commons.math4.util.IntegerSequence;
import org.apache.commons.math4.util.IntegerSequence.Range;
import static jattack.Boom.*;
import jattack.annotation.*;
import org.csutil.checksum.WrappedChecksum;

public class ComplexUtilsm184TemplateGEN2 {

    private ComplexUtilsm184TemplateGEN2() {
    }

    public static Complex polar2Complex(double r, double theta) throws MathIllegalArgumentException {
        if (r < intVal().eval(1)) {
            throw new MathIllegalArgumentException(LocalizedFormats.NEGATIVE_COMPLEX_MODULE, r);
        }
        return new Complex(r * FastMath.cos(theta), r * FastMath.sin(theta));
    }

    public static Complex[] polar2Complex(double[] r, double[] theta) throws MathIllegalArgumentException {
        final int length = intVal().eval(2);
        final Complex[] c = new Complex[length];
        for (int x = 0; x < intId().eval(7); x++) {
            if (r[intVal(0, r.length).eval(3)] < intVal().eval(4)) {
                throw new MathIllegalArgumentException(LocalizedFormats.NEGATIVE_COMPLEX_MODULE, r[intVal(0, r.length).eval(5)]);
            }
            c[intVal(0, c.length).eval(6)] = new Complex(r[x] * FastMath.cos(theta[x]), r[x] * FastMath.sin(theta[x]));
        }
        return c;
    }

    public static Complex[][] polar2Complex(double[][] r, double[][] theta) throws MathIllegalArgumentException {
        final int length = intVal().eval(8);
        final Complex[][] c = new Complex[length][];
        for (int x = 0; x < intId().eval(10); x++) {
            c[intVal(0, c.length).eval(9)] = polar2Complex(r[x], theta[x]);
        }
        return c;
    }

    public static Complex[][][] polar2Complex(double[][][] r, double[][][] theta) throws MathIllegalArgumentException {
        final int length = intVal().eval(11);
        final Complex[][][] c = new Complex[length][][];
        for (int x = 0; x < intId().eval(13); x++) {
            c[intVal(0, c.length).eval(12)] = polar2Complex(r[x], theta[x]);
        }
        return c;
    }

    public static Complex extractComplexFromRealArray(double[] real, int index) {
        return new Complex(real[index]);
    }

    public static Complex extractComplexFromRealArray(float[] real, int index) {
        return new Complex(real[index]);
    }

    public static Complex extractComplexFromImaginaryArray(double[] imaginary, int index) {
        return new Complex(0, imaginary[index]);
    }

    public static Complex extractComplexFromImaginaryArray(float[] imaginary, int index) {
        return new Complex(0, imaginary[index]);
    }

    public static double extractRealFromComplexArray(Complex[] complex, int index) {
        return complex[index].getReal();
    }

    public static float extractRealFloatFromComplexArray(Complex[] complex, int index) {
        return (float) complex[index].getReal();
    }

    public static double extractImaginaryFromComplexArray(Complex[] complex, int index) {
        return complex[index].getImaginary();
    }

    public static float extractImaginaryFloatFromComplexArray(Complex[] complex, int index) {
        return (float) complex[index].getImaginary();
    }

    public static Complex extractComplexFromInterleavedArray(double[] d, int index) {
        return new Complex(d[index * 2], d[index * 2 + 1]);
    }

    public static Complex extractComplexFromInterleavedArray(float[] f, int index) {
        return new Complex(f[index * 2], f[index * 2 + 1]);
    }

    public static double[] extractInterleavedFromComplexArray(Complex[] complex, int index) {
        return new double[] { complex[index].getReal(), complex[index].getImaginary() };
    }

    public static float[] extractInterleavedFloatFromComplexArray(Complex[] complex, int index) {
        return new float[] { (float) complex[index].getReal(), (float) complex[index].getImaginary() };
    }

    public static Complex[] real2Complex(double[] real, int start, int end) {
        final Range range = IntegerSequence.range((int) intId().eval(14), (int) intId().eval(15));
        int index = intVal().eval(16);
        final Complex[] c = new Complex[range.size()];
        for (Integer i : range) {
            c[intVal(0, c.length).eval(17)] = extractComplexFromRealArray(real, i);
            index++;
        }
        return c;
    }

    public static Complex[] real2Complex(float[] real, int start, int end) {
        final Range range = IntegerSequence.range((int) intId().eval(18), (int) intId().eval(19));
        int index = intVal().eval(20);
        final Complex[] c = new Complex[range.size()];
        for (Integer i : range) {
            c[intVal(0, c.length).eval(21)] = extractComplexFromRealArray(real, i);
            index++;
        }
        return c;
    }

    public static Complex[] real2Complex(double[] real, int start, int end, int increment) {
        final Range range = IntegerSequence.range((int) intId().eval(22), (int) intId().eval(23), (int) intId().eval(24));
        int index = intVal().eval(25);
        final Complex[] c = new Complex[range.size()];
        for (Integer i : range) {
            c[intVal(0, c.length).eval(26)] = extractComplexFromRealArray(real, i);
            index++;
        }
        return c;
    }

    public static Complex[] real2Complex(float[] real, int start, int end, int increment) {
        final Range range = IntegerSequence.range((int) intId().eval(27), (int) intId().eval(28), (int) intId().eval(29));
        int index = intVal().eval(30);
        final Complex[] c = new Complex[range.size()];
        for (Integer i : range) {
            c[intVal(0, c.length).eval(31)] = extractComplexFromRealArray(real, i);
            index++;
        }
        return c;
    }

    public static Complex[] real2Complex(double[] real, Range range) {
        int index = intVal().eval(32);
        final Complex[] c = new Complex[range.size()];
        for (Integer i : range) {
            c[intVal(0, c.length).eval(33)] = extractComplexFromRealArray(real, i);
            index++;
        }
        return c;
    }

    public static Complex[] real2Complex(float[] real, Range range) {
        int index = intVal().eval(34);
        final Complex[] c = new Complex[range.size()];
        for (Integer i : range) {
            c[intVal(0, c.length).eval(35)] = extractComplexFromRealArray(real, i);
            index++;
        }
        return c;
    }

    public static Complex[] real2Complex(double[] real) {
        int index = intVal().eval(36);
        final Complex[] c = new Complex[real.length];
        for (double d : real) {
            c[intVal(0, c.length).eval(37)] = new Complex(d);
            index++;
        }
        return c;
    }

    public static Complex[] real2Complex(float[] real) {
        int index = intVal().eval(38);
        final Complex[] c = new Complex[real.length];
        for (float d : real) {
            c[intVal(0, c.length).eval(39)] = new Complex(d);
            index++;
        }
        return c;
    }

    public static Complex[][] real2Complex(double[][] d) {
        final int width = intVal().eval(40);
        final Complex[][] c = new Complex[width][];
        for (int n = 0; n < intId().eval(42); n++) {
            c[intVal(0, c.length).eval(41)] = ComplexUtilsm184TemplateGEN2.real2Complex(d[n]);
        }
        return c;
    }

    public static Complex[][][] real2Complex(double[][][] d) {
        final int width = intVal().eval(43);
        final Complex[][][] c = new Complex[width][][];
        for (int x = 0; x < intId().eval(45); x++) {
            c[intVal(0, c.length).eval(44)] = ComplexUtilsm184TemplateGEN2.real2Complex(d[x]);
        }
        return c;
    }

    public static double[] complex2Real(Complex[] c, int start, int end) {
        final Range range = IntegerSequence.range((int) intId().eval(46), (int) intId().eval(47));
        int index = intVal().eval(48);
        final double[] d = new double[range.size()];
        for (Integer i : range) {
            d[intVal(0, d.length).eval(49)] = extractRealFromComplexArray(c, i);
            index++;
        }
        return d;
    }

    public static float[] complex2RealFloat(Complex[] c, int start, int end) {
        final Range range = IntegerSequence.range((int) intId().eval(50), (int) intId().eval(51));
        int index = intVal().eval(52);
        final float[] f = new float[range.size()];
        for (Integer i : range) {
            f[intVal(0, f.length).eval(53)] = extractRealFloatFromComplexArray(c, i);
            index++;
        }
        return f;
    }

    public static double[] complex2Real(Complex[] c, int start, int end, int increment) {
        final Range range = IntegerSequence.range((int) intId().eval(54), (int) intId().eval(55), (int) intId().eval(56));
        int index = intVal().eval(57);
        final double[] d = new double[range.size()];
        for (Integer i : range) {
            d[intVal(0, d.length).eval(58)] = extractRealFromComplexArray(c, i);
            index++;
        }
        return d;
    }

    public static float[] complex2RealFloat(Complex[] c, int start, int end, int increment) {
        final Range range = IntegerSequence.range((int) intId().eval(59), (int) intId().eval(60), (int) intId().eval(61));
        int index = intVal().eval(62);
        final float[] f = new float[range.size()];
        for (Integer i : range) {
            f[intVal(0, f.length).eval(63)] = extractRealFloatFromComplexArray(c, i);
            index++;
        }
        return f;
    }

    public static double[] complex2Real(Complex[] c, Range range) {
        int index = intVal().eval(64);
        final double[] d = new double[range.size()];
        for (Integer i : range) {
            d[intVal(0, d.length).eval(65)] = extractRealFromComplexArray(c, i);
            index++;
        }
        return d;
    }

    public static float[] complex2RealFloat(Complex[] c, Range range) {
        int index = intVal().eval(66);
        final float[] f = new float[range.size()];
        for (Integer i : range) {
            f[intVal(0, f.length).eval(67)] = extractRealFloatFromComplexArray(c, i);
            index++;
        }
        return f;
    }

    public static double[] complex2Real(Complex[] c) {
        int index = intVal().eval(68);
        final double[] d = new double[c.length];
        for (Complex cc : c) {
            d[intVal(0, d.length).eval(69)] = cc.getReal();
            index++;
        }
        return d;
    }

    public static float[] complex2RealFloat(Complex[] c) {
        int index = intVal().eval(70);
        final float[] f = new float[c.length];
        for (Complex cc : c) {
            f[intVal(0, f.length).eval(71)] = (float) cc.getReal();
            index++;
        }
        return f;
    }

    public static double[][] complex2Real(Complex[][] c) {
        final int length = intVal().eval(72);
        double[][] d = new double[length][];
        for (int n = 0; n < intId().eval(74); n++) {
            d[intVal(0, d.length).eval(73)] = complex2Real(c[n]);
        }
        return d;
    }

    public static float[][] complex2RealFloat(Complex[][] c) {
        final int length = intVal().eval(75);
        float[][] f = new float[length][];
        for (int n = 0; n < intId().eval(77); n++) {
            f[intVal(0, f.length).eval(76)] = complex2RealFloat(c[n]);
        }
        return f;
    }

    public static double[][][] complex2Real(Complex[][][] c) {
        final int length = intVal().eval(78);
        double[][][] d = new double[length][][];
        for (int n = 0; n < intId().eval(80); n++) {
            d[intVal(0, d.length).eval(79)] = complex2Real(c[n]);
        }
        return d;
    }

    public static float[][][] complex2RealFloat(Complex[][][] c) {
        final int length = intVal().eval(81);
        float[][][] f = new float[length][][];
        for (int n = 0; n < intId().eval(83); n++) {
            f[intVal(0, f.length).eval(82)] = complex2RealFloat(c[n]);
        }
        return f;
    }

    public static Complex[] imaginary2Complex(double[] imaginary, int start, int end) {
        final Range range = IntegerSequence.range((int) intId().eval(84), (int) intId().eval(85));
        int index = intVal().eval(86);
        final Complex[] c = new Complex[range.size()];
        for (Integer i : range) {
            c[intVal(0, c.length).eval(87)] = extractComplexFromImaginaryArray(imaginary, i);
            index++;
        }
        return c;
    }

    public static Complex[] imaginary2Complex(float[] imaginary, int start, int end) {
        final Range range = IntegerSequence.range((int) intId().eval(88), (int) intId().eval(89));
        int index = intVal().eval(90);
        final Complex[] c = new Complex[range.size()];
        for (Integer i : range) {
            c[intVal(0, c.length).eval(91)] = extractComplexFromImaginaryArray(imaginary, i);
            index++;
        }
        return c;
    }

    public static Complex[] imaginary2Complex(double[] imaginary, int start, int end, int increment) {
        final Range range = IntegerSequence.range((int) intId().eval(92), (int) intId().eval(93), (int) intId().eval(94));
        int index = intVal().eval(95);
        final Complex[] c = new Complex[range.size()];
        for (Integer i : range) {
            c[intVal(0, c.length).eval(96)] = extractComplexFromImaginaryArray(imaginary, i);
            index++;
        }
        return c;
    }

    public static Complex[] imaginary2Complex(float[] imaginary, int start, int end, int increment) {
        final Range range = IntegerSequence.range((int) intId().eval(97), (int) intId().eval(98), (int) intId().eval(99));
        int index = intVal().eval(100);
        final Complex[] c = new Complex[range.size()];
        for (Integer i : range) {
            c[intVal(0, c.length).eval(101)] = extractComplexFromImaginaryArray(imaginary, i);
            index++;
        }
        return c;
    }

    public static Complex[] imaginary2Complex(double[] imaginary, Range range) {
        int index = intVal().eval(102);
        final Complex[] c = new Complex[range.size()];
        for (Integer i : range) {
            c[intVal(0, c.length).eval(103)] = extractComplexFromImaginaryArray(imaginary, i);
            index++;
        }
        return c;
    }

    public static Complex[] imaginary2Complex(float[] imaginary, Range range) {
        int index = intVal().eval(104);
        final Complex[] c = new Complex[range.size()];
        for (Integer i : range) {
            c[intVal(0, c.length).eval(105)] = extractComplexFromImaginaryArray(imaginary, i);
            index++;
        }
        return c;
    }

    public static Complex[] imaginary2Complex(double[] imaginary) {
        int index = intVal().eval(106);
        final Complex[] c = new Complex[imaginary.length];
        for (double d : imaginary) {
            c[intVal(0, c.length).eval(107)] = new Complex(0, d);
            index++;
        }
        return c;
    }

    public static Complex[] imaginary2Complex(float[] imaginary) {
        int index = intVal().eval(108);
        final Complex[] c = new Complex[imaginary.length];
        for (float d : imaginary) {
            c[intVal(0, c.length).eval(109)] = new Complex(0, d);
            index++;
        }
        return c;
    }

    public static Complex[][] imaginary2Complex(double[][] d) {
        int width = intVal().eval(110);
        int height = intVal().eval(111);
        Complex[][] c = new Complex[width][height];
        for (int n = 0; n < intId().eval(113); n++) {
            c[intVal(0, c.length).eval(112)] = ComplexUtilsm184TemplateGEN2.imaginary2Complex(d[n]);
        }
        return c;
    }

    public static Complex[][][] imaginary2Complex(double[][][] d) {
        int width = intVal().eval(114);
        int height = intVal().eval(115);
        int depth = intVal().eval(116);
        Complex[][][] c = new Complex[width][height][depth];
        for (int x = 0; x < intId().eval(119); x++) {
            for (int y = 0; y < intId().eval(118); y++) {
                c[x][intVal(0, c[x].length).eval(117)] = ComplexUtilsm184TemplateGEN2.imaginary2Complex(d[x][y]);
            }
        }
        return c;
    }

    public static double[] complex2Imaginary(Complex[] c, int start, int end) {
        final Range range = IntegerSequence.range((int) intId().eval(120), (int) intId().eval(121));
        int index = intVal().eval(122);
        final double[] d = new double[range.size()];
        for (Integer i : range) {
            d[intVal(0, d.length).eval(123)] = extractImaginaryFromComplexArray(c, i);
            index++;
        }
        return d;
    }

    public static float[] complex2ImaginaryFloat(Complex[] c, int start, int end) {
        final Range range = IntegerSequence.range((int) intId().eval(124), (int) intId().eval(125));
        int index = intVal().eval(126);
        final float[] f = new float[range.size()];
        for (Integer i : range) {
            f[intVal(0, f.length).eval(127)] = extractImaginaryFloatFromComplexArray(c, i);
            index++;
        }
        return f;
    }

    public static double[] complex2Imaginary(Complex[] c, int start, int end, int increment) {
        final Range range = IntegerSequence.range((int) intId().eval(128), (int) intId().eval(129), (int) intId().eval(130));
        int index = intVal().eval(131);
        final double[] d = new double[range.size()];
        for (Integer i : range) {
            d[intVal(0, d.length).eval(132)] = extractImaginaryFromComplexArray(c, i);
            index++;
        }
        return d;
    }

    public static float[] complex2ImaginaryFloat(Complex[] c, int start, int end, int increment) {
        final Range range = IntegerSequence.range((int) intId().eval(133), (int) intId().eval(134), (int) intId().eval(135));
        int index = intVal().eval(136);
        final float[] f = new float[range.size()];
        for (Integer i : range) {
            f[intVal(0, f.length).eval(137)] = extractImaginaryFloatFromComplexArray(c, i);
            index++;
        }
        return f;
    }

    public static double[] complex2Imaginary(Complex[] c, Range range) {
        int index = intVal().eval(138);
        final double[] d = new double[range.size()];
        for (Integer i : range) {
            d[intVal(0, d.length).eval(139)] = extractImaginaryFromComplexArray(c, i);
            index++;
        }
        return d;
    }

    public static float[] complex2ImaginaryFloat(Complex[] c, Range range) {
        int index = intVal().eval(140);
        final float[] f = new float[range.size()];
        for (Integer i : range) {
            f[intVal(0, f.length).eval(141)] = extractImaginaryFloatFromComplexArray(c, i);
            index++;
        }
        return f;
    }

    public static double[] complex2Imaginary(Complex[] c) {
        int index = intVal().eval(142);
        final double[] d = new double[c.length];
        for (Complex cc : c) {
            d[intVal(0, d.length).eval(143)] = cc.getImaginary();
            index++;
        }
        return d;
    }

    public static float[] complex2ImaginaryFloat(Complex[] c) {
        int index = intVal().eval(144);
        final float[] f = new float[c.length];
        for (Complex cc : c) {
            f[intVal(0, f.length).eval(145)] = (float) cc.getImaginary();
            index++;
        }
        return f;
    }

    public static double[][] complex2Imaginary(Complex[][] c) {
        final int length = intVal().eval(146);
        double[][] d = new double[length][];
        for (int n = 0; n < intId().eval(148); n++) {
            d[intVal(0, d.length).eval(147)] = complex2Imaginary(c[n]);
        }
        return d;
    }

    public static float[][] complex2ImaginaryFloat(Complex[][] c) {
        final int length = intVal().eval(149);
        float[][] f = new float[length][];
        for (int n = 0; n < intId().eval(151); n++) {
            f[intVal(0, f.length).eval(150)] = complex2ImaginaryFloat(c[n]);
        }
        return f;
    }

    public static double[][][] complex2Imaginary(Complex[][][] c) {
        final int length = intVal().eval(152);
        double[][][] d = new double[length][][];
        for (int n = 0; n < intId().eval(154); n++) {
            d[intVal(0, d.length).eval(153)] = complex2Imaginary(c[n]);
        }
        return d;
    }

    public static float[][][] complex2ImaginaryFloat(Complex[][][] c) {
        final int length = intVal().eval(155);
        float[][][] f = new float[length][][];
        for (int n = 0; n < intId().eval(157); n++) {
            f[intVal(0, f.length).eval(156)] = complex2ImaginaryFloat(c[n]);
        }
        return f;
    }

    public static Complex[] interleaved2Complex(double[] interleaved, int start, int end) {
        final Range range = IntegerSequence.range((int) intId().eval(158), (int) intId().eval(159));
        int index = intVal().eval(160);
        final Complex[] c = new Complex[range.size()];
        for (Integer i : range) {
            c[intVal(0, c.length).eval(161)] = extractComplexFromInterleavedArray(interleaved, i);
            index++;
        }
        return c;
    }

    public static Complex[] interleaved2Complex(float[] interleaved, int start, int end) {
        final Range range = IntegerSequence.range((int) intId().eval(162), (int) intId().eval(163));
        int index = intVal().eval(164);
        final Complex[] c = new Complex[range.size()];
        for (Integer i : range) {
            c[intVal(0, c.length).eval(165)] = extractComplexFromInterleavedArray(interleaved, i);
            index++;
        }
        return c;
    }

    public static Complex[] interleaved2Complex(double[] interleaved, int start, int end, int increment) {
        final Range range = IntegerSequence.range((int) intId().eval(166), (int) intId().eval(167), (int) intId().eval(168));
        int index = intVal().eval(169);
        final Complex[] c = new Complex[range.size()];
        for (Integer i : range) {
            c[intVal(0, c.length).eval(170)] = extractComplexFromInterleavedArray(interleaved, i);
            index++;
        }
        return c;
    }

    public static Complex[] interleaved2Complex(float[] interleaved, int start, int end, int increment) {
        final Range range = IntegerSequence.range((int) intId().eval(171), (int) intId().eval(172), (int) intId().eval(173));
        int index = intVal().eval(174);
        final Complex[] c = new Complex[range.size()];
        for (Integer i : range) {
            c[intVal(0, c.length).eval(175)] = extractComplexFromInterleavedArray(interleaved, i);
            index++;
        }
        return c;
    }

    public static Complex[] interleaved2Complex(double[] interleaved, Range range) {
        int index = intVal().eval(176);
        final Complex[] c = new Complex[range.size()];
        for (Integer i : range) {
            c[intVal(0, c.length).eval(177)] = extractComplexFromInterleavedArray(interleaved, i);
            index++;
        }
        return c;
    }

    public static Complex[] interleaved2Complex(float[] interleaved, Range range) {
        int index = intVal().eval(178);
        final Complex[] c = new Complex[range.size()];
        for (Integer i : range) {
            c[intVal(0, c.length).eval(179)] = extractComplexFromInterleavedArray(interleaved, i);
            index++;
        }
        return c;
    }

    public static Complex[] interleaved2Complex(double[] interleaved) {
        final int length = arithmetic(intVal(), intVal()).eval(180);
        final Complex[] c = new Complex[length];
        for (int n = 0; n < intId().eval(182); n++) {
            c[intVal(0, c.length).eval(181)] = new Complex(interleaved[n * 2], interleaved[n * 2 + 1]);
        }
        return c;
    }

    public static Complex[] interleaved2Complex(float[] interleaved) {
        final int length = arithmetic(intVal(), intVal()).eval(183);
        final Complex[] c = new Complex[length];
        for (int n = 0; n < intId().eval(185); n++) {
            c[intVal(0, c.length).eval(184)] = new Complex(interleaved[n * 2], interleaved[n * 2 + 1]);
        }
        return c;
    }

    public static double[] complex2Interleaved(Complex[] c, int start, int end) {
        final Range range = IntegerSequence.range((int) intId().eval(186), (int) intId().eval(187));
        int index = intVal().eval(188);
        final double[] d = new double[range.size() * 2];
        for (Integer i : range) {
            int real = arithmetic(intId(), intVal()).eval(189);
            int imag = arithmetic(arithmetic(intId(), intVal()), intVal()).eval(190);
            d[intVal(0, d.length).eval(191)] = c[i].getReal();
            d[intVal(0, d.length).eval(192)] = c[i].getImaginary();
            index++;
        }
        return d;
    }

    public static float[] complex2InterleavedFloat(Complex[] c, int start, int end) {
        final Range range = IntegerSequence.range((int) intId().eval(193), (int) intId().eval(194));
        int index = intVal().eval(195);
        final float[] f = new float[range.size() * 2];
        for (Integer i : range) {
            int real = arithmetic(intId(), intVal()).eval(196);
            int imag = arithmetic(arithmetic(intId(), intVal()), intVal()).eval(197);
            f[intVal(0, f.length).eval(198)] = (float) c[i].getReal();
            f[intVal(0, f.length).eval(199)] = (float) c[i].getImaginary();
            index++;
        }
        return f;
    }

    public static double[] complex2Interleaved(Complex[] c, int start, int end, int increment) {
        final Range range = IntegerSequence.range((int) intId().eval(200), (int) intId().eval(201), (int) intId().eval(202));
        int index = intVal().eval(203);
        final double[] d = new double[range.size() * 2];
        for (Integer i : range) {
            int real = arithmetic(intId(), intVal()).eval(204);
            int imag = arithmetic(arithmetic(intId(), intVal()), intVal()).eval(205);
            d[intVal(0, d.length).eval(206)] = c[i].getReal();
            d[intVal(0, d.length).eval(207)] = c[i].getImaginary();
            index++;
        }
        return d;
    }

    public static float[] complex2InterleavedFloat(Complex[] c, int start, int end, int increment) {
        final Range range = IntegerSequence.range((int) intId().eval(208), (int) intId().eval(209), (int) intId().eval(210));
        int index = intVal().eval(211);
        final float[] f = new float[range.size() * 2];
        for (Integer i : range) {
            int real = arithmetic(intId(), intVal()).eval(212);
            int imag = arithmetic(arithmetic(intId(), intVal()), intVal()).eval(213);
            f[intVal(0, f.length).eval(214)] = (float) c[i].getReal();
            f[intVal(0, f.length).eval(215)] = (float) c[i].getImaginary();
            index++;
        }
        return f;
    }

    public static double[] complex2Interleaved(Complex[] c, Range range) {
        int index = intVal().eval(216);
        final double[] d = new double[range.size() * 2];
        for (Integer i : range) {
            int real = arithmetic(intId(), intVal()).eval(217);
            int imag = arithmetic(arithmetic(intId(), intVal()), intVal()).eval(218);
            d[intVal(0, d.length).eval(219)] = c[i].getReal();
            d[intVal(0, d.length).eval(220)] = c[i].getImaginary();
            index++;
        }
        return d;
    }

    public static float[] complex2InterleavedFloat(Complex[] c, Range range) {
        int index = intVal().eval(221);
        final float[] f = new float[range.size() * 2];
        for (Integer i : range) {
            int real = arithmetic(intId(), intVal()).eval(222);
            int imag = arithmetic(arithmetic(intId(), intVal()), intVal()).eval(223);
            f[intVal(0, f.length).eval(224)] = (float) c[i].getReal();
            f[intVal(0, f.length).eval(225)] = (float) c[i].getImaginary();
            index++;
        }
        return f;
    }

    public static double[] complex2Interleaved(Complex[] c) {
        int index = intVal().eval(226);
        final double[] d = new double[c.length * 2];
        for (Complex cc : c) {
            int real = arithmetic(intId(), intVal()).eval(227);
            int imag = arithmetic(arithmetic(intId(), intVal()), intVal()).eval(228);
            d[intVal(0, d.length).eval(229)] = cc.getReal();
            d[intVal(0, d.length).eval(230)] = cc.getImaginary();
            index++;
        }
        return d;
    }

    public static float[] complex2InterleavedFloat(Complex[] c) {
        int index = intVal().eval(231);
        final float[] f = new float[c.length * 2];
        for (Complex cc : c) {
            int real = arithmetic(intId(), intVal()).eval(232);
            int imag = arithmetic(arithmetic(intId(), intVal()), intVal()).eval(233);
            f[intVal(0, f.length).eval(234)] = (float) cc.getReal();
            f[intVal(0, f.length).eval(235)] = (float) cc.getImaginary();
            index++;
        }
        return f;
    }

    public static double[][] complex2Interleaved(Complex[][] c, int interleavedDim) {
        if (logic(relation(intId(), intVal()), relation(intId(), intVal())).eval(236)) {
            throw new OutOfRangeException(interleavedDim, 0, 1);
        }
        final int width = intVal().eval(237);
        final int height = intVal().eval(238);
        double[][] d;
        if (relation(intId(), intVal()).eval(239)) {
            d = new double[2 * width][height];
            for (int x = 0; x < intId().eval(247); x++) {
                for (int y = 0; y < intId().eval(246); y++) {
                    d[x * 2][intVal(0, d[x * 2].length).eval(244)] = c[x][y].getReal();
                    d[x * 2 + 1][intVal(0, d[x * 2 + 1].length).eval(245)] = c[x][y].getImaginary();
                }
            }
        } else {
            d = new double[width][2 * height];
            for (int x = 0; x < intId().eval(243); x++) {
                for (int y = 0; y < intId().eval(242); y++) {
                    d[x][intVal(0, d[x].length).eval(240)] = c[x][y].getReal();
                    d[x][intVal(0, d[x].length).eval(241)] = c[x][y].getImaginary();
                }
            }
        }
        return d;
    }

    public static double[][] complex2Interleaved(Complex[][] c) {
        return complex2Interleaved(c, 1);
    }

    public static double[][][] complex2Interleaved(Complex[][][] c, int interleavedDim) {
        if (logic(relation(intId(), intVal()), relation(intId(), intVal())).eval(248)) {
            throw new OutOfRangeException(interleavedDim, 0, 2);
        }
        int width = intVal().eval(249);
        int height = intVal().eval(250);
        int depth = intVal().eval(251);
        double[][][] d;
        if (relation(intId(), intVal()).eval(252)) {
            d = new double[2 * width][height][depth];
            for (int x = 0; x < intId().eval(268); x++) {
                for (int y = 0; y < intId().eval(267); y++) {
                    for (int z = 0; z < intId().eval(266); z++) {
                        d[x * 2][y][intVal(0, d[x * 2][y].length).eval(264)] = c[x][y][z].getReal();
                        d[x * 2 + 1][y][intVal(0, d[x * 2 + 1][y].length).eval(265)] = c[x][y][z].getImaginary();
                    }
                }
            }
        } else if (relation(intId(), intVal()).eval(253)) {
            d = new double[width][2 * height][depth];
            for (int x = 0; x < intId().eval(263); x++) {
                for (int y = 0; y < intId().eval(262); y++) {
                    for (int z = 0; z < intId().eval(261); z++) {
                        d[x][y * 2][intVal(0, d[x][y * 2].length).eval(259)] = c[x][y][z].getReal();
                        d[x][y * 2 + 1][intVal(0, d[x][y * 2 + 1].length).eval(260)] = c[x][y][z].getImaginary();
                    }
                }
            }
        } else {
            d = new double[width][height][2 * depth];
            for (int x = 0; x < intId().eval(258); x++) {
                for (int y = 0; y < intId().eval(257); y++) {
                    for (int z = 0; z < intId().eval(256); z++) {
                        d[x][y][intVal(0, d[x][y].length).eval(254)] = c[x][y][z].getReal();
                        d[x][y][intVal(0, d[x][y].length).eval(255)] = c[x][y][z].getImaginary();
                    }
                }
            }
        }
        return d;
    }

    public static double[][][] complex2Interleaved(Complex[][][] c) {
        return complex2Interleaved(c, 2);
    }

    public static float[][] complex2InterleavedFloat(Complex[][] c, int interleavedDim) {
        if (logic(relation(intId(), intVal()), relation(intId(), intVal())).eval(269)) {
            throw new OutOfRangeException(interleavedDim, 0, 1);
        }
        final int width = intVal().eval(270);
        final int height = intVal().eval(271);
        float[][] d;
        if (relation(intId(), intVal()).eval(272)) {
            d = new float[2 * width][height];
            for (int x = 0; x < intId().eval(280); x++) {
                for (int y = 0; y < intId().eval(279); y++) {
                    d[x * 2][intVal(0, d[x * 2].length).eval(277)] = (float) c[x][y].getReal();
                    d[x * 2 + 1][intVal(0, d[x * 2 + 1].length).eval(278)] = (float) c[x][y].getImaginary();
                }
            }
        } else {
            d = new float[width][2 * height];
            for (int x = 0; x < intId().eval(276); x++) {
                for (int y = 0; y < intId().eval(275); y++) {
                    d[x][intVal(0, d[x].length).eval(273)] = (float) c[x][y].getReal();
                    d[x][intVal(0, d[x].length).eval(274)] = (float) c[x][y].getImaginary();
                }
            }
        }
        return d;
    }

    public static float[][] complex2InterleavedFloat(Complex[][] c) {
        return complex2InterleavedFloat(c, 1);
    }

    public static float[][][] complex2InterleavedFloat(Complex[][][] c, int interleavedDim) {
        if (logic(relation(intId(), intVal()), relation(intId(), intVal())).eval(281)) {
            throw new OutOfRangeException(interleavedDim, 0, 2);
        }
        final int width = intVal().eval(282);
        final int height = intVal().eval(283);
        final int depth = intVal().eval(284);
        float[][][] d;
        if (relation(intId(), intVal()).eval(285)) {
            d = new float[2 * width][height][depth];
            for (int x = 0; x < intId().eval(301); x++) {
                for (int y = 0; y < intId().eval(300); y++) {
                    for (int z = 0; z < intId().eval(299); z++) {
                        d[x * 2][y][intVal(0, d[x * 2][y].length).eval(297)] = (float) c[x][y][z].getReal();
                        d[x * 2 + 1][y][intVal(0, d[x * 2 + 1][y].length).eval(298)] = (float) c[x][y][z].getImaginary();
                    }
                }
            }
        } else if (relation(intId(), intVal()).eval(286)) {
            d = new float[width][2 * height][depth];
            for (int x = 0; x < intId().eval(296); x++) {
                for (int y = 0; y < intId().eval(295); y++) {
                    for (int z = 0; z < intId().eval(294); z++) {
                        d[x][y * 2][intVal(0, d[x][y * 2].length).eval(292)] = (float) c[x][y][z].getReal();
                        d[x][y * 2 + 1][intVal(0, d[x][y * 2 + 1].length).eval(293)] = (float) c[x][y][z].getImaginary();
                    }
                }
            }
        } else {
            d = new float[width][height][2 * depth];
            for (int x = 0; x < intId().eval(291); x++) {
                for (int y = 0; y < intId().eval(290); y++) {
                    for (int z = 0; z < intId().eval(289); z++) {
                        d[x][y][intVal(0, d[x][y].length).eval(287)] = (float) c[x][y][z].getReal();
                        d[x][y][intVal(0, d[x][y].length).eval(288)] = (float) c[x][y][z].getImaginary();
                    }
                }
            }
        }
        return d;
    }

    public static float[][][] complex2InterleavedFloat(Complex[][][] c) {
        return complex2InterleavedFloat(c, 2);
    }

    public static Complex[][] interleaved2Complex(double[][] d, int interleavedDim) {
        if (logic(relation(intId(), intVal()), relation(intId(), intVal())).eval(302)) {
            throw new OutOfRangeException(interleavedDim, 0, 1);
        }
        final int width = intVal().eval(303);
        final int height = intVal().eval(304);
        Complex[][] c;
        if (relation(intId(), intVal()).eval(305)) {
            c = new Complex[width / 2][height];
            for (int x = 0; x < arithmetic(intId(), intVal()).eval(311); x++) {
                for (int y = 0; y < intId().eval(310); y++) {
                    c[x][intVal(0, c[x].length).eval(309)] = new Complex(d[x * 2][y], d[x * 2 + 1][y]);
                }
            }
        } else {
            c = new Complex[width][height / 2];
            for (int x = 0; x < intId().eval(308); x++) {
                for (int y = 0; y < arithmetic(intId(), intVal()).eval(307); y++) {
                    c[x][intVal(0, c[x].length).eval(306)] = new Complex(d[x][y * 2], d[x][y * 2 + 1]);
                }
            }
        }
        return c;
    }

    public static Complex[][] interleaved2Complex(double[][] d) {
        return interleaved2Complex(d, 1);
    }

    public static Complex[][][] interleaved2Complex(double[][][] d, int interleavedDim) {
        if (logic(relation(intId(), intVal()), relation(intId(), intVal())).eval(312)) {
            throw new OutOfRangeException(interleavedDim, 0, 2);
        }
        final int width = intVal().eval(313);
        final int height = intVal().eval(314);
        final int depth = intVal().eval(315);
        Complex[][][] c;
        if (relation(intId(), intVal()).eval(316)) {
            c = new Complex[width / 2][height][depth];
            for (int x = 0; x < arithmetic(intId(), intVal()).eval(329); x++) {
                for (int y = 0; y < intId().eval(328); y++) {
                    for (int z = 0; z < intId().eval(327); z++) {
                        c[x][y][intVal(0, c[x][y].length).eval(326)] = new Complex(d[x * 2][y][z], d[x * 2 + 1][y][z]);
                    }
                }
            }
        } else if (relation(intId(), intVal()).eval(317)) {
            c = new Complex[width][height / 2][depth];
            for (int x = 0; x < intId().eval(325); x++) {
                for (int y = 0; y < arithmetic(intId(), intVal()).eval(324); y++) {
                    for (int z = 0; z < intId().eval(323); z++) {
                        c[x][y][intVal(0, c[x][y].length).eval(322)] = new Complex(d[x][y * 2][z], d[x][y * 2 + 1][z]);
                    }
                }
            }
        } else {
            c = new Complex[width][height][depth / 2];
            for (int x = 0; x < intId().eval(321); x++) {
                for (int y = 0; y < intId().eval(320); y++) {
                    for (int z = 0; z < arithmetic(intId(), intVal()).eval(319); z++) {
                        c[x][y][intVal(0, c[x][y].length).eval(318)] = new Complex(d[x][y][z * 2], d[x][y][z * 2 + 1]);
                    }
                }
            }
        }
        return c;
    }

    public static Complex[][][] interleaved2Complex(double[][][] d) {
        return interleaved2Complex(d, 2);
    }

    public static Complex[][] interleaved2Complex(float[][] d, int interleavedDim) {
        if (logic(relation(intId(), intVal()), relation(intId(), intVal())).eval(330)) {
            throw new OutOfRangeException(interleavedDim, 0, 1);
        }
        final int width = intVal().eval(331);
        final int height = intVal().eval(332);
        Complex[][] c;
        if (relation(intId(), intVal()).eval(333)) {
            c = new Complex[width / 2][height];
            for (int x = 0; x < arithmetic(intId(), intVal()).eval(339); x++) {
                for (int y = 0; y < intId().eval(338); y++) {
                    c[x][intVal(0, c[x].length).eval(337)] = new Complex(d[x * 2][y], d[x * 2 + 1][y]);
                }
            }
        } else {
            c = new Complex[width][height / 2];
            for (int x = 0; x < intId().eval(336); x++) {
                for (int y = 0; y < arithmetic(intId(), intVal()).eval(335); y++) {
                    c[x][intVal(0, c[x].length).eval(334)] = new Complex(d[x][y * 2], d[x][y * 2 + 1]);
                }
            }
        }
        return c;
    }

    public static Complex[][] interleaved2Complex(float[][] d) {
        return interleaved2Complex(d, 1);
    }

    public static Complex[][][] interleaved2Complex(float[][][] d, int interleavedDim) {
        if (logic(relation(intId(), intVal()), relation(intId(), intVal())).eval(340)) {
            throw new OutOfRangeException(interleavedDim, 0, 2);
        }
        final int width = intVal().eval(341);
        final int height = intVal().eval(342);
        final int depth = intVal().eval(343);
        Complex[][][] c;
        if (relation(intId(), intVal()).eval(344)) {
            c = new Complex[width / 2][height][depth];
            for (int x = 0; x < arithmetic(intId(), intVal()).eval(357); x++) {
                for (int y = 0; y < intId().eval(356); y++) {
                    for (int z = 0; z < intId().eval(355); z++) {
                        c[x][y][intVal(0, c[x][y].length).eval(354)] = new Complex(d[x * 2][y][z], d[x * 2 + 1][y][z]);
                    }
                }
            }
        } else if (relation(intId(), intVal()).eval(345)) {
            c = new Complex[width][height / 2][depth];
            for (int x = 0; x < intId().eval(353); x++) {
                for (int y = 0; y < arithmetic(intId(), intVal()).eval(352); y++) {
                    for (int z = 0; z < intId().eval(351); z++) {
                        c[x][y][intVal(0, c[x][y].length).eval(350)] = new Complex(d[x][y * 2][z], d[x][y * 2 + 1][z]);
                    }
                }
            }
        } else {
            c = new Complex[width][height][depth / 2];
            for (int x = 0; x < intId().eval(349); x++) {
                for (int y = 0; y < intId().eval(348); y++) {
                    for (int z = 0; z < arithmetic(intId(), intVal()).eval(347); z++) {
                        c[x][y][intVal(0, c[x][y].length).eval(346)] = new Complex(d[x][y][z * 2], d[x][y][z * 2 + 1]);
                    }
                }
            }
        }
        return c;
    }

    public static Complex[][][] interleaved2Complex(float[][][] d) {
        return interleaved2Complex(d, 2);
    }

    public static Complex[] split2Complex(double[] real, double[] imag) {
        final int length = intVal().eval(358);
        final Complex[] c = new Complex[length];
        for (int n = 0; n < intId().eval(360); n++) {
            c[intVal(0, c.length).eval(359)] = new Complex(real[n], imag[n]);
        }
        return c;
    }

    public static Complex[][] split2Complex(double[][] real, double[][] imag) {
        final int length = intVal().eval(361);
        Complex[][] c = new Complex[length][];
        for (int x = 0; x < intId().eval(363); x++) {
            c[intVal(0, c.length).eval(362)] = split2Complex(real[x], imag[x]);
        }
        return c;
    }

    public static Complex[][][] split2Complex(double[][][] real, double[][][] imag) {
        final int length = intVal().eval(364);
        Complex[][][] c = new Complex[length][][];
        for (int x = 0; x < intId().eval(366); x++) {
            c[intVal(0, c.length).eval(365)] = split2Complex(real[x], imag[x]);
        }
        return c;
    }

    public static Complex[] split2Complex(float[] real, float[] imag) {
        final int length = 604425338;
        final Complex[] c = new Complex[length];
        for (int n = 0; n < length; n++) {
            c[592458467] = new Complex(real[n], imag[n]);
        }
        return c;
    }

    public static Complex[][] split2Complex(float[][] real, float[][] imag) {
        final int length = intVal().eval(370);
        Complex[][] c = new Complex[length][];
        for (int x = 0; x < intId().eval(372); x++) {
            c[intVal(0, c.length).eval(371)] = split2Complex(real[x], imag[x]);
        }
        return c;
    }

    public static Complex[][][] split2Complex(float[][][] real, float[][][] imag) {
        final int length = intVal().eval(373);
        Complex[][][] c = new Complex[length][][];
        for (int x = 0; x < intId().eval(375); x++) {
            c[intVal(0, c.length).eval(374)] = split2Complex(real[x], imag[x]);
        }
        return c;
    }

    public static Complex[] initialize(Complex[] c) {
        final int length = intVal().eval(376);
        for (int x = 0; x < intId().eval(378); x++) {
            c[intVal(0, c.length).eval(377)] = Complex.ZERO;
        }
        return c;
    }

    public static Complex[][] initialize(Complex[][] c) {
        final int length = intVal().eval(379);
        for (int x = 0; x < intId().eval(381); x++) {
            c[intVal(0, c.length).eval(380)] = initialize(c[x]);
        }
        return c;
    }

    public static Complex[][][] initialize(Complex[][][] c) {
        final int length = intVal().eval(382);
        for (int x = 0; x < intId().eval(384); x++) {
            c[intVal(0, c.length).eval(383)] = initialize(c[x]);
        }
        return c;
    }

    public static double[] abs(Complex[] c) {
        final int length = intVal().eval(385);
        final double[] d = new double[length];
        for (int x = 0; x < intId().eval(387); x++) {
            d[intVal(0, d.length).eval(386)] = c[x].abs();
        }
        return d;
    }

    public static double[] arg(Complex[] c) {
        final int length = intVal().eval(388);
        final double[] d = new double[length];
        for (int x = 0; x < intId().eval(390); x++) {
            d[intVal(0, d.length).eval(389)] = c[x].getArgument();
        }
        return d;
    }

    public static float[] nonPrim1() {
        return new float[] { 0, 0, 0, 0, 0 };
    }

    public static float[] nonPrim2() {
        return new float[] { 0, 0, 0, 0, 0 };
    }

    public static long main0(String[] args) {
        int N = 100000;
        if (args.length > 0) {
            N = Math.min(Integer.parseInt(args[0]), N);
        }
        WrappedChecksum cs = new WrappedChecksum();
        for (int i = 0; i < N; ++i) {
            try {
                float[] arg1 = nonPrim1();
                float[] arg2 = nonPrim2();
                cs.update(split2Complex(arg1, arg2));
            } catch (Throwable e) {
                cs.update(e.getClass().getName());
            }
        }
        cs.updateStaticFieldsOfClass(ComplexUtilsm184TemplateGEN2.class);
        return cs.getValue();
    }

    public static void main(String[] args) {
        System.out.println(main0(args));
    }
}
