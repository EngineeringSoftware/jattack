package org.apache.commons.math4.complex;

import org.apache.commons.math4.exception.MathIllegalArgumentException;
import org.apache.commons.math4.exception.OutOfRangeException;
import org.apache.commons.math4.exception.util.LocalizedFormats;
import org.apache.commons.math4.util.FastMath;
import org.apache.commons.math4.util.IntegerSequence;
import org.apache.commons.math4.util.IntegerSequence.Range;
import static jattack.Boom.*;
import jattack.annotation.*;

public class ComplexUtilsm184Template {

    private ComplexUtilsm184Template() {
    }

    public static Complex polar2Complex(double r, double theta) throws MathIllegalArgumentException {
        if (r < intVal().eval()) {
            throw new MathIllegalArgumentException(LocalizedFormats.NEGATIVE_COMPLEX_MODULE, r);
        }
        return new Complex(r * FastMath.cos(theta), r * FastMath.sin(theta));
    }

    public static Complex[] polar2Complex(double[] r, double[] theta) throws MathIllegalArgumentException {
        final int length = intVal().eval();
        final Complex[] c = new Complex[length];
        for (int x = 0; x < intId().eval(); x++) {
            if (r[intVal(0, r.length).eval()] < intVal().eval()) {
                throw new MathIllegalArgumentException(LocalizedFormats.NEGATIVE_COMPLEX_MODULE, r[intVal(0, r.length).eval()]);
            }
            c[intVal(0, c.length).eval()] = new Complex(r[x] * FastMath.cos(theta[x]), r[x] * FastMath.sin(theta[x]));
        }
        return c;
    }

    public static Complex[][] polar2Complex(double[][] r, double[][] theta) throws MathIllegalArgumentException {
        final int length = intVal().eval();
        final Complex[][] c = new Complex[length][];
        for (int x = 0; x < intId().eval(); x++) {
            c[intVal(0, c.length).eval()] = polar2Complex(r[x], theta[x]);
        }
        return c;
    }

    public static Complex[][][] polar2Complex(double[][][] r, double[][][] theta) throws MathIllegalArgumentException {
        final int length = intVal().eval();
        final Complex[][][] c = new Complex[length][][];
        for (int x = 0; x < intId().eval(); x++) {
            c[intVal(0, c.length).eval()] = polar2Complex(r[x], theta[x]);
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
        final Range range = IntegerSequence.range((int) intId().eval(), (int) intId().eval());
        int index = intVal().eval();
        final Complex[] c = new Complex[range.size()];
        for (Integer i : range) {
            c[intVal(0, c.length).eval()] = extractComplexFromRealArray(real, i);
            index++;
        }
        return c;
    }

    public static Complex[] real2Complex(float[] real, int start, int end) {
        final Range range = IntegerSequence.range((int) intId().eval(), (int) intId().eval());
        int index = intVal().eval();
        final Complex[] c = new Complex[range.size()];
        for (Integer i : range) {
            c[intVal(0, c.length).eval()] = extractComplexFromRealArray(real, i);
            index++;
        }
        return c;
    }

    public static Complex[] real2Complex(double[] real, int start, int end, int increment) {
        final Range range = IntegerSequence.range((int) intId().eval(), (int) intId().eval(), (int) intId().eval());
        int index = intVal().eval();
        final Complex[] c = new Complex[range.size()];
        for (Integer i : range) {
            c[intVal(0, c.length).eval()] = extractComplexFromRealArray(real, i);
            index++;
        }
        return c;
    }

    public static Complex[] real2Complex(float[] real, int start, int end, int increment) {
        final Range range = IntegerSequence.range((int) intId().eval(), (int) intId().eval(), (int) intId().eval());
        int index = intVal().eval();
        final Complex[] c = new Complex[range.size()];
        for (Integer i : range) {
            c[intVal(0, c.length).eval()] = extractComplexFromRealArray(real, i);
            index++;
        }
        return c;
    }

    public static Complex[] real2Complex(double[] real, Range range) {
        int index = intVal().eval();
        final Complex[] c = new Complex[range.size()];
        for (Integer i : range) {
            c[intVal(0, c.length).eval()] = extractComplexFromRealArray(real, i);
            index++;
        }
        return c;
    }

    public static Complex[] real2Complex(float[] real, Range range) {
        int index = intVal().eval();
        final Complex[] c = new Complex[range.size()];
        for (Integer i : range) {
            c[intVal(0, c.length).eval()] = extractComplexFromRealArray(real, i);
            index++;
        }
        return c;
    }

    public static Complex[] real2Complex(double[] real) {
        int index = intVal().eval();
        final Complex[] c = new Complex[real.length];
        for (double d : real) {
            c[intVal(0, c.length).eval()] = new Complex(d);
            index++;
        }
        return c;
    }

    public static Complex[] real2Complex(float[] real) {
        int index = intVal().eval();
        final Complex[] c = new Complex[real.length];
        for (float d : real) {
            c[intVal(0, c.length).eval()] = new Complex(d);
            index++;
        }
        return c;
    }

    public static Complex[][] real2Complex(double[][] d) {
        final int width = intVal().eval();
        final Complex[][] c = new Complex[width][];
        for (int n = 0; n < intId().eval(); n++) {
            c[intVal(0, c.length).eval()] = ComplexUtilsm184Template.real2Complex(d[n]);
        }
        return c;
    }

    public static Complex[][][] real2Complex(double[][][] d) {
        final int width = intVal().eval();
        final Complex[][][] c = new Complex[width][][];
        for (int x = 0; x < intId().eval(); x++) {
            c[intVal(0, c.length).eval()] = ComplexUtilsm184Template.real2Complex(d[x]);
        }
        return c;
    }

    public static double[] complex2Real(Complex[] c, int start, int end) {
        final Range range = IntegerSequence.range((int) intId().eval(), (int) intId().eval());
        int index = intVal().eval();
        final double[] d = new double[range.size()];
        for (Integer i : range) {
            d[intVal(0, d.length).eval()] = extractRealFromComplexArray(c, i);
            index++;
        }
        return d;
    }

    public static float[] complex2RealFloat(Complex[] c, int start, int end) {
        final Range range = IntegerSequence.range((int) intId().eval(), (int) intId().eval());
        int index = intVal().eval();
        final float[] f = new float[range.size()];
        for (Integer i : range) {
            f[intVal(0, f.length).eval()] = extractRealFloatFromComplexArray(c, i);
            index++;
        }
        return f;
    }

    public static double[] complex2Real(Complex[] c, int start, int end, int increment) {
        final Range range = IntegerSequence.range((int) intId().eval(), (int) intId().eval(), (int) intId().eval());
        int index = intVal().eval();
        final double[] d = new double[range.size()];
        for (Integer i : range) {
            d[intVal(0, d.length).eval()] = extractRealFromComplexArray(c, i);
            index++;
        }
        return d;
    }

    public static float[] complex2RealFloat(Complex[] c, int start, int end, int increment) {
        final Range range = IntegerSequence.range((int) intId().eval(), (int) intId().eval(), (int) intId().eval());
        int index = intVal().eval();
        final float[] f = new float[range.size()];
        for (Integer i : range) {
            f[intVal(0, f.length).eval()] = extractRealFloatFromComplexArray(c, i);
            index++;
        }
        return f;
    }

    public static double[] complex2Real(Complex[] c, Range range) {
        int index = intVal().eval();
        final double[] d = new double[range.size()];
        for (Integer i : range) {
            d[intVal(0, d.length).eval()] = extractRealFromComplexArray(c, i);
            index++;
        }
        return d;
    }

    public static float[] complex2RealFloat(Complex[] c, Range range) {
        int index = intVal().eval();
        final float[] f = new float[range.size()];
        for (Integer i : range) {
            f[intVal(0, f.length).eval()] = extractRealFloatFromComplexArray(c, i);
            index++;
        }
        return f;
    }

    public static double[] complex2Real(Complex[] c) {
        int index = intVal().eval();
        final double[] d = new double[c.length];
        for (Complex cc : c) {
            d[intVal(0, d.length).eval()] = cc.getReal();
            index++;
        }
        return d;
    }

    public static float[] complex2RealFloat(Complex[] c) {
        int index = intVal().eval();
        final float[] f = new float[c.length];
        for (Complex cc : c) {
            f[intVal(0, f.length).eval()] = (float) cc.getReal();
            index++;
        }
        return f;
    }

    public static double[][] complex2Real(Complex[][] c) {
        final int length = intVal().eval();
        double[][] d = new double[length][];
        for (int n = 0; n < intId().eval(); n++) {
            d[intVal(0, d.length).eval()] = complex2Real(c[n]);
        }
        return d;
    }

    public static float[][] complex2RealFloat(Complex[][] c) {
        final int length = intVal().eval();
        float[][] f = new float[length][];
        for (int n = 0; n < intId().eval(); n++) {
            f[intVal(0, f.length).eval()] = complex2RealFloat(c[n]);
        }
        return f;
    }

    public static double[][][] complex2Real(Complex[][][] c) {
        final int length = intVal().eval();
        double[][][] d = new double[length][][];
        for (int n = 0; n < intId().eval(); n++) {
            d[intVal(0, d.length).eval()] = complex2Real(c[n]);
        }
        return d;
    }

    public static float[][][] complex2RealFloat(Complex[][][] c) {
        final int length = intVal().eval();
        float[][][] f = new float[length][][];
        for (int n = 0; n < intId().eval(); n++) {
            f[intVal(0, f.length).eval()] = complex2RealFloat(c[n]);
        }
        return f;
    }

    public static Complex[] imaginary2Complex(double[] imaginary, int start, int end) {
        final Range range = IntegerSequence.range((int) intId().eval(), (int) intId().eval());
        int index = intVal().eval();
        final Complex[] c = new Complex[range.size()];
        for (Integer i : range) {
            c[intVal(0, c.length).eval()] = extractComplexFromImaginaryArray(imaginary, i);
            index++;
        }
        return c;
    }

    public static Complex[] imaginary2Complex(float[] imaginary, int start, int end) {
        final Range range = IntegerSequence.range((int) intId().eval(), (int) intId().eval());
        int index = intVal().eval();
        final Complex[] c = new Complex[range.size()];
        for (Integer i : range) {
            c[intVal(0, c.length).eval()] = extractComplexFromImaginaryArray(imaginary, i);
            index++;
        }
        return c;
    }

    public static Complex[] imaginary2Complex(double[] imaginary, int start, int end, int increment) {
        final Range range = IntegerSequence.range((int) intId().eval(), (int) intId().eval(), (int) intId().eval());
        int index = intVal().eval();
        final Complex[] c = new Complex[range.size()];
        for (Integer i : range) {
            c[intVal(0, c.length).eval()] = extractComplexFromImaginaryArray(imaginary, i);
            index++;
        }
        return c;
    }

    public static Complex[] imaginary2Complex(float[] imaginary, int start, int end, int increment) {
        final Range range = IntegerSequence.range((int) intId().eval(), (int) intId().eval(), (int) intId().eval());
        int index = intVal().eval();
        final Complex[] c = new Complex[range.size()];
        for (Integer i : range) {
            c[intVal(0, c.length).eval()] = extractComplexFromImaginaryArray(imaginary, i);
            index++;
        }
        return c;
    }

    public static Complex[] imaginary2Complex(double[] imaginary, Range range) {
        int index = intVal().eval();
        final Complex[] c = new Complex[range.size()];
        for (Integer i : range) {
            c[intVal(0, c.length).eval()] = extractComplexFromImaginaryArray(imaginary, i);
            index++;
        }
        return c;
    }

    public static Complex[] imaginary2Complex(float[] imaginary, Range range) {
        int index = intVal().eval();
        final Complex[] c = new Complex[range.size()];
        for (Integer i : range) {
            c[intVal(0, c.length).eval()] = extractComplexFromImaginaryArray(imaginary, i);
            index++;
        }
        return c;
    }

    public static Complex[] imaginary2Complex(double[] imaginary) {
        int index = intVal().eval();
        final Complex[] c = new Complex[imaginary.length];
        for (double d : imaginary) {
            c[intVal(0, c.length).eval()] = new Complex(0, d);
            index++;
        }
        return c;
    }

    public static Complex[] imaginary2Complex(float[] imaginary) {
        int index = intVal().eval();
        final Complex[] c = new Complex[imaginary.length];
        for (float d : imaginary) {
            c[intVal(0, c.length).eval()] = new Complex(0, d);
            index++;
        }
        return c;
    }

    public static Complex[][] imaginary2Complex(double[][] d) {
        int width = intVal().eval();
        int height = intVal().eval();
        Complex[][] c = new Complex[width][height];
        for (int n = 0; n < intId().eval(); n++) {
            c[intVal(0, c.length).eval()] = ComplexUtilsm184Template.imaginary2Complex(d[n]);
        }
        return c;
    }

    public static Complex[][][] imaginary2Complex(double[][][] d) {
        int width = intVal().eval();
        int height = intVal().eval();
        int depth = intVal().eval();
        Complex[][][] c = new Complex[width][height][depth];
        for (int x = 0; x < intId().eval(); x++) {
            for (int y = 0; y < intId().eval(); y++) {
                c[x][intVal(0, c[x].length).eval()] = ComplexUtilsm184Template.imaginary2Complex(d[x][y]);
            }
        }
        return c;
    }

    public static double[] complex2Imaginary(Complex[] c, int start, int end) {
        final Range range = IntegerSequence.range((int) intId().eval(), (int) intId().eval());
        int index = intVal().eval();
        final double[] d = new double[range.size()];
        for (Integer i : range) {
            d[intVal(0, d.length).eval()] = extractImaginaryFromComplexArray(c, i);
            index++;
        }
        return d;
    }

    public static float[] complex2ImaginaryFloat(Complex[] c, int start, int end) {
        final Range range = IntegerSequence.range((int) intId().eval(), (int) intId().eval());
        int index = intVal().eval();
        final float[] f = new float[range.size()];
        for (Integer i : range) {
            f[intVal(0, f.length).eval()] = extractImaginaryFloatFromComplexArray(c, i);
            index++;
        }
        return f;
    }

    public static double[] complex2Imaginary(Complex[] c, int start, int end, int increment) {
        final Range range = IntegerSequence.range((int) intId().eval(), (int) intId().eval(), (int) intId().eval());
        int index = intVal().eval();
        final double[] d = new double[range.size()];
        for (Integer i : range) {
            d[intVal(0, d.length).eval()] = extractImaginaryFromComplexArray(c, i);
            index++;
        }
        return d;
    }

    public static float[] complex2ImaginaryFloat(Complex[] c, int start, int end, int increment) {
        final Range range = IntegerSequence.range((int) intId().eval(), (int) intId().eval(), (int) intId().eval());
        int index = intVal().eval();
        final float[] f = new float[range.size()];
        for (Integer i : range) {
            f[intVal(0, f.length).eval()] = extractImaginaryFloatFromComplexArray(c, i);
            index++;
        }
        return f;
    }

    public static double[] complex2Imaginary(Complex[] c, Range range) {
        int index = intVal().eval();
        final double[] d = new double[range.size()];
        for (Integer i : range) {
            d[intVal(0, d.length).eval()] = extractImaginaryFromComplexArray(c, i);
            index++;
        }
        return d;
    }

    public static float[] complex2ImaginaryFloat(Complex[] c, Range range) {
        int index = intVal().eval();
        final float[] f = new float[range.size()];
        for (Integer i : range) {
            f[intVal(0, f.length).eval()] = extractImaginaryFloatFromComplexArray(c, i);
            index++;
        }
        return f;
    }

    public static double[] complex2Imaginary(Complex[] c) {
        int index = intVal().eval();
        final double[] d = new double[c.length];
        for (Complex cc : c) {
            d[intVal(0, d.length).eval()] = cc.getImaginary();
            index++;
        }
        return d;
    }

    public static float[] complex2ImaginaryFloat(Complex[] c) {
        int index = intVal().eval();
        final float[] f = new float[c.length];
        for (Complex cc : c) {
            f[intVal(0, f.length).eval()] = (float) cc.getImaginary();
            index++;
        }
        return f;
    }

    public static double[][] complex2Imaginary(Complex[][] c) {
        final int length = intVal().eval();
        double[][] d = new double[length][];
        for (int n = 0; n < intId().eval(); n++) {
            d[intVal(0, d.length).eval()] = complex2Imaginary(c[n]);
        }
        return d;
    }

    public static float[][] complex2ImaginaryFloat(Complex[][] c) {
        final int length = intVal().eval();
        float[][] f = new float[length][];
        for (int n = 0; n < intId().eval(); n++) {
            f[intVal(0, f.length).eval()] = complex2ImaginaryFloat(c[n]);
        }
        return f;
    }

    public static double[][][] complex2Imaginary(Complex[][][] c) {
        final int length = intVal().eval();
        double[][][] d = new double[length][][];
        for (int n = 0; n < intId().eval(); n++) {
            d[intVal(0, d.length).eval()] = complex2Imaginary(c[n]);
        }
        return d;
    }

    public static float[][][] complex2ImaginaryFloat(Complex[][][] c) {
        final int length = intVal().eval();
        float[][][] f = new float[length][][];
        for (int n = 0; n < intId().eval(); n++) {
            f[intVal(0, f.length).eval()] = complex2ImaginaryFloat(c[n]);
        }
        return f;
    }

    public static Complex[] interleaved2Complex(double[] interleaved, int start, int end) {
        final Range range = IntegerSequence.range((int) intId().eval(), (int) intId().eval());
        int index = intVal().eval();
        final Complex[] c = new Complex[range.size()];
        for (Integer i : range) {
            c[intVal(0, c.length).eval()] = extractComplexFromInterleavedArray(interleaved, i);
            index++;
        }
        return c;
    }

    public static Complex[] interleaved2Complex(float[] interleaved, int start, int end) {
        final Range range = IntegerSequence.range((int) intId().eval(), (int) intId().eval());
        int index = intVal().eval();
        final Complex[] c = new Complex[range.size()];
        for (Integer i : range) {
            c[intVal(0, c.length).eval()] = extractComplexFromInterleavedArray(interleaved, i);
            index++;
        }
        return c;
    }

    public static Complex[] interleaved2Complex(double[] interleaved, int start, int end, int increment) {
        final Range range = IntegerSequence.range((int) intId().eval(), (int) intId().eval(), (int) intId().eval());
        int index = intVal().eval();
        final Complex[] c = new Complex[range.size()];
        for (Integer i : range) {
            c[intVal(0, c.length).eval()] = extractComplexFromInterleavedArray(interleaved, i);
            index++;
        }
        return c;
    }

    public static Complex[] interleaved2Complex(float[] interleaved, int start, int end, int increment) {
        final Range range = IntegerSequence.range((int) intId().eval(), (int) intId().eval(), (int) intId().eval());
        int index = intVal().eval();
        final Complex[] c = new Complex[range.size()];
        for (Integer i : range) {
            c[intVal(0, c.length).eval()] = extractComplexFromInterleavedArray(interleaved, i);
            index++;
        }
        return c;
    }

    public static Complex[] interleaved2Complex(double[] interleaved, Range range) {
        int index = intVal().eval();
        final Complex[] c = new Complex[range.size()];
        for (Integer i : range) {
            c[intVal(0, c.length).eval()] = extractComplexFromInterleavedArray(interleaved, i);
            index++;
        }
        return c;
    }

    public static Complex[] interleaved2Complex(float[] interleaved, Range range) {
        int index = intVal().eval();
        final Complex[] c = new Complex[range.size()];
        for (Integer i : range) {
            c[intVal(0, c.length).eval()] = extractComplexFromInterleavedArray(interleaved, i);
            index++;
        }
        return c;
    }

    public static Complex[] interleaved2Complex(double[] interleaved) {
        final int length = arithmetic(intVal(), intVal()).eval();
        final Complex[] c = new Complex[length];
        for (int n = 0; n < intId().eval(); n++) {
            c[intVal(0, c.length).eval()] = new Complex(interleaved[n * 2], interleaved[n * 2 + 1]);
        }
        return c;
    }

    public static Complex[] interleaved2Complex(float[] interleaved) {
        final int length = arithmetic(intVal(), intVal()).eval();
        final Complex[] c = new Complex[length];
        for (int n = 0; n < intId().eval(); n++) {
            c[intVal(0, c.length).eval()] = new Complex(interleaved[n * 2], interleaved[n * 2 + 1]);
        }
        return c;
    }

    public static double[] complex2Interleaved(Complex[] c, int start, int end) {
        final Range range = IntegerSequence.range((int) intId().eval(), (int) intId().eval());
        int index = intVal().eval();
        final double[] d = new double[range.size() * 2];
        for (Integer i : range) {
            int real = arithmetic(intId(), intVal()).eval();
            int imag = arithmetic(arithmetic(intId(), intVal()), intVal()).eval();
            d[intVal(0, d.length).eval()] = c[i].getReal();
            d[intVal(0, d.length).eval()] = c[i].getImaginary();
            index++;
        }
        return d;
    }

    public static float[] complex2InterleavedFloat(Complex[] c, int start, int end) {
        final Range range = IntegerSequence.range((int) intId().eval(), (int) intId().eval());
        int index = intVal().eval();
        final float[] f = new float[range.size() * 2];
        for (Integer i : range) {
            int real = arithmetic(intId(), intVal()).eval();
            int imag = arithmetic(arithmetic(intId(), intVal()), intVal()).eval();
            f[intVal(0, f.length).eval()] = (float) c[i].getReal();
            f[intVal(0, f.length).eval()] = (float) c[i].getImaginary();
            index++;
        }
        return f;
    }

    public static double[] complex2Interleaved(Complex[] c, int start, int end, int increment) {
        final Range range = IntegerSequence.range((int) intId().eval(), (int) intId().eval(), (int) intId().eval());
        int index = intVal().eval();
        final double[] d = new double[range.size() * 2];
        for (Integer i : range) {
            int real = arithmetic(intId(), intVal()).eval();
            int imag = arithmetic(arithmetic(intId(), intVal()), intVal()).eval();
            d[intVal(0, d.length).eval()] = c[i].getReal();
            d[intVal(0, d.length).eval()] = c[i].getImaginary();
            index++;
        }
        return d;
    }

    public static float[] complex2InterleavedFloat(Complex[] c, int start, int end, int increment) {
        final Range range = IntegerSequence.range((int) intId().eval(), (int) intId().eval(), (int) intId().eval());
        int index = intVal().eval();
        final float[] f = new float[range.size() * 2];
        for (Integer i : range) {
            int real = arithmetic(intId(), intVal()).eval();
            int imag = arithmetic(arithmetic(intId(), intVal()), intVal()).eval();
            f[intVal(0, f.length).eval()] = (float) c[i].getReal();
            f[intVal(0, f.length).eval()] = (float) c[i].getImaginary();
            index++;
        }
        return f;
    }

    public static double[] complex2Interleaved(Complex[] c, Range range) {
        int index = intVal().eval();
        final double[] d = new double[range.size() * 2];
        for (Integer i : range) {
            int real = arithmetic(intId(), intVal()).eval();
            int imag = arithmetic(arithmetic(intId(), intVal()), intVal()).eval();
            d[intVal(0, d.length).eval()] = c[i].getReal();
            d[intVal(0, d.length).eval()] = c[i].getImaginary();
            index++;
        }
        return d;
    }

    public static float[] complex2InterleavedFloat(Complex[] c, Range range) {
        int index = intVal().eval();
        final float[] f = new float[range.size() * 2];
        for (Integer i : range) {
            int real = arithmetic(intId(), intVal()).eval();
            int imag = arithmetic(arithmetic(intId(), intVal()), intVal()).eval();
            f[intVal(0, f.length).eval()] = (float) c[i].getReal();
            f[intVal(0, f.length).eval()] = (float) c[i].getImaginary();
            index++;
        }
        return f;
    }

    public static double[] complex2Interleaved(Complex[] c) {
        int index = intVal().eval();
        final double[] d = new double[c.length * 2];
        for (Complex cc : c) {
            int real = arithmetic(intId(), intVal()).eval();
            int imag = arithmetic(arithmetic(intId(), intVal()), intVal()).eval();
            d[intVal(0, d.length).eval()] = cc.getReal();
            d[intVal(0, d.length).eval()] = cc.getImaginary();
            index++;
        }
        return d;
    }

    public static float[] complex2InterleavedFloat(Complex[] c) {
        int index = intVal().eval();
        final float[] f = new float[c.length * 2];
        for (Complex cc : c) {
            int real = arithmetic(intId(), intVal()).eval();
            int imag = arithmetic(arithmetic(intId(), intVal()), intVal()).eval();
            f[intVal(0, f.length).eval()] = (float) cc.getReal();
            f[intVal(0, f.length).eval()] = (float) cc.getImaginary();
            index++;
        }
        return f;
    }

    public static double[][] complex2Interleaved(Complex[][] c, int interleavedDim) {
        if (logic(relation(intId(), intVal()), relation(intId(), intVal())).eval()) {
            throw new OutOfRangeException(interleavedDim, 0, 1);
        }
        final int width = intVal().eval();
        final int height = intVal().eval();
        double[][] d;
        if (relation(intId(), intVal()).eval()) {
            d = new double[2 * width][height];
            for (int x = 0; x < intId().eval(); x++) {
                for (int y = 0; y < intId().eval(); y++) {
                    d[x * 2][intVal(0, d[x * 2].length).eval()] = c[x][y].getReal();
                    d[x * 2 + 1][intVal(0, d[x * 2 + 1].length).eval()] = c[x][y].getImaginary();
                }
            }
        } else {
            d = new double[width][2 * height];
            for (int x = 0; x < intId().eval(); x++) {
                for (int y = 0; y < intId().eval(); y++) {
                    d[x][intVal(0, d[x].length).eval()] = c[x][y].getReal();
                    d[x][intVal(0, d[x].length).eval()] = c[x][y].getImaginary();
                }
            }
        }
        return d;
    }

    public static double[][] complex2Interleaved(Complex[][] c) {
        return complex2Interleaved(c, 1);
    }

    public static double[][][] complex2Interleaved(Complex[][][] c, int interleavedDim) {
        if (logic(relation(intId(), intVal()), relation(intId(), intVal())).eval()) {
            throw new OutOfRangeException(interleavedDim, 0, 2);
        }
        int width = intVal().eval();
        int height = intVal().eval();
        int depth = intVal().eval();
        double[][][] d;
        if (relation(intId(), intVal()).eval()) {
            d = new double[2 * width][height][depth];
            for (int x = 0; x < intId().eval(); x++) {
                for (int y = 0; y < intId().eval(); y++) {
                    for (int z = 0; z < intId().eval(); z++) {
                        d[x * 2][y][intVal(0, d[x * 2][y].length).eval()] = c[x][y][z].getReal();
                        d[x * 2 + 1][y][intVal(0, d[x * 2 + 1][y].length).eval()] = c[x][y][z].getImaginary();
                    }
                }
            }
        } else if (relation(intId(), intVal()).eval()) {
            d = new double[width][2 * height][depth];
            for (int x = 0; x < intId().eval(); x++) {
                for (int y = 0; y < intId().eval(); y++) {
                    for (int z = 0; z < intId().eval(); z++) {
                        d[x][y * 2][intVal(0, d[x][y * 2].length).eval()] = c[x][y][z].getReal();
                        d[x][y * 2 + 1][intVal(0, d[x][y * 2 + 1].length).eval()] = c[x][y][z].getImaginary();
                    }
                }
            }
        } else {
            d = new double[width][height][2 * depth];
            for (int x = 0; x < intId().eval(); x++) {
                for (int y = 0; y < intId().eval(); y++) {
                    for (int z = 0; z < intId().eval(); z++) {
                        d[x][y][intVal(0, d[x][y].length).eval()] = c[x][y][z].getReal();
                        d[x][y][intVal(0, d[x][y].length).eval()] = c[x][y][z].getImaginary();
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
        if (logic(relation(intId(), intVal()), relation(intId(), intVal())).eval()) {
            throw new OutOfRangeException(interleavedDim, 0, 1);
        }
        final int width = intVal().eval();
        final int height = intVal().eval();
        float[][] d;
        if (relation(intId(), intVal()).eval()) {
            d = new float[2 * width][height];
            for (int x = 0; x < intId().eval(); x++) {
                for (int y = 0; y < intId().eval(); y++) {
                    d[x * 2][intVal(0, d[x * 2].length).eval()] = (float) c[x][y].getReal();
                    d[x * 2 + 1][intVal(0, d[x * 2 + 1].length).eval()] = (float) c[x][y].getImaginary();
                }
            }
        } else {
            d = new float[width][2 * height];
            for (int x = 0; x < intId().eval(); x++) {
                for (int y = 0; y < intId().eval(); y++) {
                    d[x][intVal(0, d[x].length).eval()] = (float) c[x][y].getReal();
                    d[x][intVal(0, d[x].length).eval()] = (float) c[x][y].getImaginary();
                }
            }
        }
        return d;
    }

    public static float[][] complex2InterleavedFloat(Complex[][] c) {
        return complex2InterleavedFloat(c, 1);
    }

    public static float[][][] complex2InterleavedFloat(Complex[][][] c, int interleavedDim) {
        if (logic(relation(intId(), intVal()), relation(intId(), intVal())).eval()) {
            throw new OutOfRangeException(interleavedDim, 0, 2);
        }
        final int width = intVal().eval();
        final int height = intVal().eval();
        final int depth = intVal().eval();
        float[][][] d;
        if (relation(intId(), intVal()).eval()) {
            d = new float[2 * width][height][depth];
            for (int x = 0; x < intId().eval(); x++) {
                for (int y = 0; y < intId().eval(); y++) {
                    for (int z = 0; z < intId().eval(); z++) {
                        d[x * 2][y][intVal(0, d[x * 2][y].length).eval()] = (float) c[x][y][z].getReal();
                        d[x * 2 + 1][y][intVal(0, d[x * 2 + 1][y].length).eval()] = (float) c[x][y][z].getImaginary();
                    }
                }
            }
        } else if (relation(intId(), intVal()).eval()) {
            d = new float[width][2 * height][depth];
            for (int x = 0; x < intId().eval(); x++) {
                for (int y = 0; y < intId().eval(); y++) {
                    for (int z = 0; z < intId().eval(); z++) {
                        d[x][y * 2][intVal(0, d[x][y * 2].length).eval()] = (float) c[x][y][z].getReal();
                        d[x][y * 2 + 1][intVal(0, d[x][y * 2 + 1].length).eval()] = (float) c[x][y][z].getImaginary();
                    }
                }
            }
        } else {
            d = new float[width][height][2 * depth];
            for (int x = 0; x < intId().eval(); x++) {
                for (int y = 0; y < intId().eval(); y++) {
                    for (int z = 0; z < intId().eval(); z++) {
                        d[x][y][intVal(0, d[x][y].length).eval()] = (float) c[x][y][z].getReal();
                        d[x][y][intVal(0, d[x][y].length).eval()] = (float) c[x][y][z].getImaginary();
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
        if (logic(relation(intId(), intVal()), relation(intId(), intVal())).eval()) {
            throw new OutOfRangeException(interleavedDim, 0, 1);
        }
        final int width = intVal().eval();
        final int height = intVal().eval();
        Complex[][] c;
        if (relation(intId(), intVal()).eval()) {
            c = new Complex[width / 2][height];
            for (int x = 0; x < arithmetic(intId(), intVal()).eval(); x++) {
                for (int y = 0; y < intId().eval(); y++) {
                    c[x][intVal(0, c[x].length).eval()] = new Complex(d[x * 2][y], d[x * 2 + 1][y]);
                }
            }
        } else {
            c = new Complex[width][height / 2];
            for (int x = 0; x < intId().eval(); x++) {
                for (int y = 0; y < arithmetic(intId(), intVal()).eval(); y++) {
                    c[x][intVal(0, c[x].length).eval()] = new Complex(d[x][y * 2], d[x][y * 2 + 1]);
                }
            }
        }
        return c;
    }

    public static Complex[][] interleaved2Complex(double[][] d) {
        return interleaved2Complex(d, 1);
    }

    public static Complex[][][] interleaved2Complex(double[][][] d, int interleavedDim) {
        if (logic(relation(intId(), intVal()), relation(intId(), intVal())).eval()) {
            throw new OutOfRangeException(interleavedDim, 0, 2);
        }
        final int width = intVal().eval();
        final int height = intVal().eval();
        final int depth = intVal().eval();
        Complex[][][] c;
        if (relation(intId(), intVal()).eval()) {
            c = new Complex[width / 2][height][depth];
            for (int x = 0; x < arithmetic(intId(), intVal()).eval(); x++) {
                for (int y = 0; y < intId().eval(); y++) {
                    for (int z = 0; z < intId().eval(); z++) {
                        c[x][y][intVal(0, c[x][y].length).eval()] = new Complex(d[x * 2][y][z], d[x * 2 + 1][y][z]);
                    }
                }
            }
        } else if (relation(intId(), intVal()).eval()) {
            c = new Complex[width][height / 2][depth];
            for (int x = 0; x < intId().eval(); x++) {
                for (int y = 0; y < arithmetic(intId(), intVal()).eval(); y++) {
                    for (int z = 0; z < intId().eval(); z++) {
                        c[x][y][intVal(0, c[x][y].length).eval()] = new Complex(d[x][y * 2][z], d[x][y * 2 + 1][z]);
                    }
                }
            }
        } else {
            c = new Complex[width][height][depth / 2];
            for (int x = 0; x < intId().eval(); x++) {
                for (int y = 0; y < intId().eval(); y++) {
                    for (int z = 0; z < arithmetic(intId(), intVal()).eval(); z++) {
                        c[x][y][intVal(0, c[x][y].length).eval()] = new Complex(d[x][y][z * 2], d[x][y][z * 2 + 1]);
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
        if (logic(relation(intId(), intVal()), relation(intId(), intVal())).eval()) {
            throw new OutOfRangeException(interleavedDim, 0, 1);
        }
        final int width = intVal().eval();
        final int height = intVal().eval();
        Complex[][] c;
        if (relation(intId(), intVal()).eval()) {
            c = new Complex[width / 2][height];
            for (int x = 0; x < arithmetic(intId(), intVal()).eval(); x++) {
                for (int y = 0; y < intId().eval(); y++) {
                    c[x][intVal(0, c[x].length).eval()] = new Complex(d[x * 2][y], d[x * 2 + 1][y]);
                }
            }
        } else {
            c = new Complex[width][height / 2];
            for (int x = 0; x < intId().eval(); x++) {
                for (int y = 0; y < arithmetic(intId(), intVal()).eval(); y++) {
                    c[x][intVal(0, c[x].length).eval()] = new Complex(d[x][y * 2], d[x][y * 2 + 1]);
                }
            }
        }
        return c;
    }

    public static Complex[][] interleaved2Complex(float[][] d) {
        return interleaved2Complex(d, 1);
    }

    public static Complex[][][] interleaved2Complex(float[][][] d, int interleavedDim) {
        if (logic(relation(intId(), intVal()), relation(intId(), intVal())).eval()) {
            throw new OutOfRangeException(interleavedDim, 0, 2);
        }
        final int width = intVal().eval();
        final int height = intVal().eval();
        final int depth = intVal().eval();
        Complex[][][] c;
        if (relation(intId(), intVal()).eval()) {
            c = new Complex[width / 2][height][depth];
            for (int x = 0; x < arithmetic(intId(), intVal()).eval(); x++) {
                for (int y = 0; y < intId().eval(); y++) {
                    for (int z = 0; z < intId().eval(); z++) {
                        c[x][y][intVal(0, c[x][y].length).eval()] = new Complex(d[x * 2][y][z], d[x * 2 + 1][y][z]);
                    }
                }
            }
        } else if (relation(intId(), intVal()).eval()) {
            c = new Complex[width][height / 2][depth];
            for (int x = 0; x < intId().eval(); x++) {
                for (int y = 0; y < arithmetic(intId(), intVal()).eval(); y++) {
                    for (int z = 0; z < intId().eval(); z++) {
                        c[x][y][intVal(0, c[x][y].length).eval()] = new Complex(d[x][y * 2][z], d[x][y * 2 + 1][z]);
                    }
                }
            }
        } else {
            c = new Complex[width][height][depth / 2];
            for (int x = 0; x < intId().eval(); x++) {
                for (int y = 0; y < intId().eval(); y++) {
                    for (int z = 0; z < arithmetic(intId(), intVal()).eval(); z++) {
                        c[x][y][intVal(0, c[x][y].length).eval()] = new Complex(d[x][y][z * 2], d[x][y][z * 2 + 1]);
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
        final int length = intVal().eval();
        final Complex[] c = new Complex[length];
        for (int n = 0; n < intId().eval(); n++) {
            c[intVal(0, c.length).eval()] = new Complex(real[n], imag[n]);
        }
        return c;
    }

    public static Complex[][] split2Complex(double[][] real, double[][] imag) {
        final int length = intVal().eval();
        Complex[][] c = new Complex[length][];
        for (int x = 0; x < intId().eval(); x++) {
            c[intVal(0, c.length).eval()] = split2Complex(real[x], imag[x]);
        }
        return c;
    }

    public static Complex[][][] split2Complex(double[][][] real, double[][][] imag) {
        final int length = intVal().eval();
        Complex[][][] c = new Complex[length][][];
        for (int x = 0; x < intId().eval(); x++) {
            c[intVal(0, c.length).eval()] = split2Complex(real[x], imag[x]);
        }
        return c;
    }

    @jattack.annotation.Entry()
    public static Complex[] split2Complex(float[] real, float[] imag) {
        final int length = intVal().eval();
        final Complex[] c = new Complex[length];
        for (int n = 0; n < intId().eval(); n++) {
            c[intVal(0, c.length).eval()] = new Complex(real[n], imag[n]);
        }
        return c;
    }

    public static Complex[][] split2Complex(float[][] real, float[][] imag) {
        final int length = intVal().eval();
        Complex[][] c = new Complex[length][];
        for (int x = 0; x < intId().eval(); x++) {
            c[intVal(0, c.length).eval()] = split2Complex(real[x], imag[x]);
        }
        return c;
    }

    public static Complex[][][] split2Complex(float[][][] real, float[][][] imag) {
        final int length = intVal().eval();
        Complex[][][] c = new Complex[length][][];
        for (int x = 0; x < intId().eval(); x++) {
            c[intVal(0, c.length).eval()] = split2Complex(real[x], imag[x]);
        }
        return c;
    }

    public static Complex[] initialize(Complex[] c) {
        final int length = intVal().eval();
        for (int x = 0; x < intId().eval(); x++) {
            c[intVal(0, c.length).eval()] = Complex.ZERO;
        }
        return c;
    }

    public static Complex[][] initialize(Complex[][] c) {
        final int length = intVal().eval();
        for (int x = 0; x < intId().eval(); x++) {
            c[intVal(0, c.length).eval()] = initialize(c[x]);
        }
        return c;
    }

    public static Complex[][][] initialize(Complex[][][] c) {
        final int length = intVal().eval();
        for (int x = 0; x < intId().eval(); x++) {
            c[intVal(0, c.length).eval()] = initialize(c[x]);
        }
        return c;
    }

    public static double[] abs(Complex[] c) {
        final int length = intVal().eval();
        final double[] d = new double[length];
        for (int x = 0; x < intId().eval(); x++) {
            d[intVal(0, d.length).eval()] = c[x].abs();
        }
        return d;
    }

    public static double[] arg(Complex[] c) {
        final int length = intVal().eval();
        final double[] d = new double[length];
        for (int x = 0; x < intId().eval(); x++) {
            d[intVal(0, d.length).eval()] = c[x].getArgument();
        }
        return d;
    }

    @Argument(1)
    public static float[] nonPrim1() {
        return new float[] { 0, 0, 0, 0, 0 };
    }

    @Argument(2)
    public static float[] nonPrim2() {
        return new float[] { 0, 0, 0, 0, 0 };
    }
}
