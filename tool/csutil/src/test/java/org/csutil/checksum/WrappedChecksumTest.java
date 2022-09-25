package org.csutil.checksum;

import org.csutil.util.ByteBufferUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.CRC32;

public class WrappedChecksumTest {

    private WrappedChecksum checksum;
    private CRC32 refChecksum;

    @Before
    public void init() {
        checksum = new WrappedChecksum();
        refChecksum = new CRC32();
    }

    @Test
    public void testInt() {
        int i = 3;

        checksum.update(i);
        refChecksum.update(ByteBufferUtil.toByteBuffer(i));

        Assert.assertEquals("Int checksum.",
                refChecksum.getValue(), checksum.getValue());
    }

    @Test
    public void testDouble() {
        double d = 10;

        checksum.update(d);
        refChecksum.update(ByteBufferUtil.toByteBuffer(d));

        Assert.assertEquals("Double checksum.",
                refChecksum.getValue(), checksum.getValue());
    }

    @Test
    public void testString() {
        String str = "C&CRemasteredCollection";

        checksum.update(str);
        for (char c : str.toCharArray()) {
            refChecksum.update(ByteBufferUtil.toByteBuffer(c));
        }

        Assert.assertEquals("String checksum.",
                refChecksum.getValue(), checksum.getValue());
    }

    @Test
    public void testBoxedInt() {
        int i = 1;

        Integer integer = i;
        checksum.update(integer);

        refChecksum.update(ByteBufferUtil.toByteBuffer(i));

        Assert.assertEquals("Boxed primitive checksum.",
                refChecksum.getValue(), checksum.getValue());
    }

    @Test
    public void testNonPrimitiveReference() {
        F f = new F();
        D d = new D(f);
        checksum.update(d);

        refChecksum.update(ByteBufferUtil.toByteBuffer(d.di));
        refChecksum.update(ByteBufferUtil.toByteBuffer(1)); // d -> d.df
        refChecksum.update(ByteBufferUtil.toByteBuffer(d.df.fi));

        Assert.assertEquals("Non primitive reference checksum.",
                refChecksum.getValue(), checksum.getValue());
    }

    @Test
    public void testCommonObjectFields() {
        F f = new F();
        C c = new C(f);
        checksum.update(c);

        refChecksum.update(ByteBufferUtil.toByteBuffer(c.ci));
        refChecksum.update(ByteBufferUtil.toByteBuffer(1)); // c -> c.cd
        refChecksum.update(ByteBufferUtil.toByteBuffer(c.cd.di));
        refChecksum.update(ByteBufferUtil.toByteBuffer(2)); // c.cd -> c.cd.df
        refChecksum.update(ByteBufferUtil.toByteBuffer(c.cd.df.fi));
        refChecksum.update(ByteBufferUtil.toByteBuffer(3)); // c -> c.ce
        refChecksum.update(ByteBufferUtil.toByteBuffer(c.ce.di));
        refChecksum.update(ByteBufferUtil.toByteBuffer(2)); // c.ce -> c.ce.df

        Assert.assertEquals("Common object fields checksum.",
                refChecksum.getValue(), checksum.getValue());
    }

    @Test
    public void testCircularDependency() {
        G g = new G();
        H h = new H();
        g.gh = h;
        h.hg = g;
        checksum.update(g);

        refChecksum.update(ByteBufferUtil.toByteBuffer(g.gi));
        refChecksum.update(ByteBufferUtil.toByteBuffer(1)); // g -> g.gh
        refChecksum.update(ByteBufferUtil.toByteBuffer(g.gh.hi));
        refChecksum.update(ByteBufferUtil.toByteBuffer(0)); // h -> h.hg

        Assert.assertEquals("Circular dependency checksum.",
                refChecksum.getValue(), checksum.getValue());
    }

    @Test
    public void testPrimitiveArray() {
        int[] a = {1, 2, 3, 4, 5};

        checksum.update(a);

        for (int e : a) {
            refChecksum.update(ByteBufferUtil.toByteBuffer(e));
        }

        Assert.assertEquals("Primitive array checksum.",
                refChecksum.getValue(), checksum.getValue());
    }

    @Test
    public void test2DArray() {
        int[][] a = { {1, 2, 3}, {4, 5, 6}, {7, 8, 9}};

        checksum.update(a);

        for (int[] row : a) {
            for (int e : row) {
                refChecksum.update(ByteBufferUtil.toByteBuffer(e));
            }
        }

        Assert.assertEquals("2D array checksum.",
                refChecksum.getValue(), checksum.getValue());
    }

    @Test
    public void testObjectArray() {
        D[] ds = new D[5];
        for (int i = 0; i < ds.length; ++i) {
            F f = new F();
            ds[i] = new D(f);
            refChecksum.update(ByteBufferUtil.toByteBuffer(ds[i].di));
            refChecksum.update(ByteBufferUtil.toByteBuffer(1));
            refChecksum.update(ByteBufferUtil.toByteBuffer(ds[i].df.fi));
        }
        checksum.update(ds);

        Assert.assertEquals("Object array checksum.",
                refChecksum.getValue(), checksum.getValue());
    }

    @Test
    public void testObjectWithArrayField() {
        ClassWitArrayField cwaf = new ClassWitArrayField();
        checksum.update(cwaf);

        for (int i = 0; i < cwaf.fs.length; ++i) {
            F f = cwaf.fs[i];
            refChecksum.update(ByteBufferUtil.toByteBuffer(i + 1));
            refChecksum.update(ByteBufferUtil.toByteBuffer(f.fi));
        }

        Assert.assertEquals("Object with array field checksum.",
                refChecksum.getValue(), checksum.getValue());
    }

    @Test
    @Ignore
    public void testList() {
        List<Integer> l = Arrays.asList(1, 2, 3, 4 ,5 ,6);
        checksum.update(l);

        for (int e : l) {
            refChecksum.update(ByteBufferUtil.toByteBuffer(e));
        }

        Assert.assertEquals("List checksum.",
                refChecksum.getValue(), checksum.getValue());
    }

    @Test
    @Ignore
    public void testListAsField() {
        ClassWithCollectionField cwcf = new ClassWithCollectionField();
        checksum.update(cwcf);

        for (int i = 0; i < cwcf.fs.size(); ++i) {
            F f = cwcf.fs.get(i);
            refChecksum.update(ByteBufferUtil.toByteBuffer(i + 1));
            refChecksum.update(ByteBufferUtil.toByteBuffer(f.fi));
        }

        Assert.assertEquals("List as field checksum.",
                refChecksum.getValue(), checksum.getValue());
    }

    @Test
    @Ignore
    public void testSet() {
        Set<Integer> s = new HashSet<>(Arrays.asList(1, 2, 3));
        checksum.update(s);

        for (int e : s) {
            refChecksum.update(ByteBufferUtil.toByteBuffer(e));
        }

        Assert.assertEquals("Set checksum.",
                refChecksum.getValue(), checksum.getValue());
    }

    @Test
    @Ignore
    public void testMap() {
        Map<Integer, F> m = new TreeMap<>();
        m.put(2, new F());
        m.put(3, new F());
        m.put(1, new F());
        checksum.update(m);

        for (Map.Entry<Integer, F> e : m.entrySet()) {
            int k = e.getKey();
            F v = e.getValue();
            refChecksum.update(ByteBufferUtil.toByteBuffer(k));
            refChecksum.update(ByteBufferUtil.toByteBuffer(v.fi));
        }

        Assert.assertEquals("Map checksum.",
                refChecksum.getValue(), checksum.getValue());
    }

    @Test
    public void testObjectThatIsIgnoredClass() {
        Exception e = new RuntimeException("Warning: Lightning storm created.");
        checksum.update(e);

        Assert.assertEquals("Ignored class checksum.",
                refChecksum.getValue(), checksum.getValue());
    }

    @Test
    public void testObjectWithIgnoredClassField() {
        ClassWithIgnoredClassField cwice = new ClassWithIgnoredClassField();
        checksum.update(cwice);

        refChecksum.update(ByteBufferUtil.toByteBuffer(cwice.i));
        refChecksum.update(ByteBufferUtil.toByteBuffer(1)); // cwice -> cwice.e

        Assert.assertEquals("Object with ignored class field checksum.",
                refChecksum.getValue(), checksum.getValue());
    }

    @Test
    public void testObjectWithIgnoredClassArrayField() {
        ClassWithIgnoredClassArrayField cwicaf = new ClassWithIgnoredClassArrayField();
        checksum.update(cwicaf);

        for (int i = 0; i < cwicaf.es.length; ++i) {
            Exception e = cwicaf.es[i];
            refChecksum.update(ByteBufferUtil.toByteBuffer(i + 1)); // cwice -> cwice.e
        }

        Assert.assertEquals("Object with ignored class field checksum.",
                refChecksum.getValue(), checksum.getValue());
    }

    @Test
    public void testSubclassOfIgnoredClass() {
        SubException se = new SubException();
        checksum.update(se);

        refChecksum.update(ByteBufferUtil.toByteBuffer(se.i));

        Assert.assertEquals("Subclass of ignored classes checksum.",
                refChecksum.getValue(), checksum.getValue());
    }

    /************************
       Classes for testing.
    *************************/

    private static class SubException extends Exception {
        int i;

        SubException() {
            i = 1;
        }
    }

    private static class ClassWithIgnoredClassArrayField {
        Exception[] es;

        ClassWithIgnoredClassArrayField() {
            es = new RuntimeException[5];
            for (int i = 0; i < es.length; ++i) {
                es[i] = new RuntimeException("Warning: Psychic dominator activated.");
            }
        }
    }

    private static class ClassWithIgnoredClassField {
        Exception e;
        int i;

        ClassWithIgnoredClassField() {
            e = new RuntimeException("Warning: Nuclear missile launched.");
            i = 1;
        }
    }

    private static class ClassWithCollectionField {
        List<F> fs;

        ClassWithCollectionField() {
            fs = new ArrayList<>();
            fs.addAll(Arrays.asList(new F(), new F(), new F(), new F()));
        }
    }

    private static class ClassWitArrayField {
        F[] fs;

        ClassWitArrayField() {
            fs = new F[5];
            for (int i = 0; i < fs.length; ++i) {
                fs[i] = new F();
            }
        }
    }

    private static class C {
        int ci;
        D cd;
        E ce;

        C(F f) {
            ci = 1;
            cd = new D(f);
            ce = new E(f);
        }
    }

    private static class D {
        int di;
        F df;

        D(F f) {
            di = 2;
            df = f;
        }
    }

    private static class E extends D {
        E(F f) {
            super(f);
        }
    }

    private static class F {
        int fi;

        F() {
            fi = 3;
        }
    }

    private static class G {
        int gi = 4;
        H gh;
    }

    private static class H {
        int hi = 5;
        G hg;
    }
}
