package org.csutil.checksum;

import org.csutil.log.Log;
import org.csutil.util.ByteBufferUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.Checksum;

public class WrappedChecksumTest {

    private WrappedChecksum checksum;
    private Checksum refChecksum;

    @Before
    public void init() {
        checksum = new WrappedChecksum();
        refChecksum = WrappedChecksum.newChecksum();
        // Log.setLevel(Log.Level.DEBUG);
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

        refChecksum.update(ByteBufferUtil.toByteBuffer(0)); // d
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

        refChecksum.update(ByteBufferUtil.toByteBuffer(0)); // c
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
    public void testCircularDependencyWithField() {
        G g = new G();
        H h = new H();
        g.gh = h;
        h.hg = g;
        checksum.update(g);

        refChecksum.update(ByteBufferUtil.toByteBuffer(0)); // g
        refChecksum.update(ByteBufferUtil.toByteBuffer(g.gi));
        refChecksum.update(ByteBufferUtil.toByteBuffer(1)); // g -> g.gh
        refChecksum.update(ByteBufferUtil.toByteBuffer(g.gh.hi));
        refChecksum.update(ByteBufferUtil.toByteBuffer(0)); // h -> h.hg

        Assert.assertEquals("Circular dependency with field checksum.",
                refChecksum.getValue(), checksum.getValue());
    }

    @Test
    public void testCircularDependencyWithMoreThanOneInDegrees() {
        J j = new J();
        K k = new K();
        k.kj = j;
        M m = new M();
        m.mj = j;
        j.jk = k;
        j.jm = m;
        checksum.update(j);

        refChecksum.update(ByteBufferUtil.toByteBuffer(0)); // j
        refChecksum.update(ByteBufferUtil.toByteBuffer(1)); // j -> j.jk
        refChecksum.update(ByteBufferUtil.toByteBuffer(k.ki)); // k.ki
        refChecksum.update(ByteBufferUtil.toByteBuffer(0)); // k -> k.kj
        refChecksum.update(ByteBufferUtil.toByteBuffer(2)); // j -> j.jm
        refChecksum.update(ByteBufferUtil.toByteBuffer(m.mi)); // m.mi
        refChecksum.update(ByteBufferUtil.toByteBuffer(0)); // m -> m.mj

        Assert.assertEquals("Circular dependency with more than one in degrees checksum.",
                            refChecksum.getValue(), checksum.getValue());
    }

    @Test
    public void testCircularDependencyWithFieldAndOrderedContainer() {
        ClassWithListOfObject c = new ClassWithListOfObject();
        ClassWithRefToParent cwrtp = new ClassWithRefToParent();
        cwrtp.parent = c;
        c.list = List.of(1, cwrtp, 2);

        checksum.update(c);

        refChecksum.update(ByteBufferUtil.toByteBuffer(0)); // c
        refChecksum.update(ByteBufferUtil.toByteBuffer(1)); // c -> c.list
        refChecksum.update(ByteBufferUtil.toByteBuffer(1)); // c.list[0], i.e., 1
        refChecksum.update(ByteBufferUtil.toByteBuffer(2)); // c.list -> c.list[1], i.e., cwrtp
        refChecksum.update(ByteBufferUtil.toByteBuffer(0)); // cwrtp -> c
        refChecksum.update(ByteBufferUtil.toByteBuffer(2)); // c.list[2], i.e., 2

        Assert.assertEquals("Circular dependency with field and ordered container checksum.",
                            refChecksum.getValue(), checksum.getValue());
    }

    @Test
    public void testCircularDependencyWithFieldAndUnorderedContainer() {
        ClassWithSetOfObject c = new ClassWithSetOfObject();
        ClassWithRefToParent cwrtp = new ClassWithRefToParent();
        cwrtp.parent = c;
        c.set = new HashSet<>(List.of(1, cwrtp, 2));

        checksum.update(c);

        refChecksum.update(ByteBufferUtil.toByteBuffer(0)); // c
        refChecksum.update(ByteBufferUtil.toByteBuffer(1)); // c -> c.set
        long sum = 0;
        Checksum currChecksum = WrappedChecksum.newChecksum();
        // 1
        currChecksum.update(ByteBufferUtil.toByteBuffer(1));
        sum += currChecksum.getValue();
        // cwrtp
        currChecksum.reset();
        currChecksum.update(ByteBufferUtil.toByteBuffer(0)); // cwrtp
        currChecksum.update(ByteBufferUtil.toByteBuffer(1)); // cwrtp -> c, note uid reset
        sum += currChecksum.getValue();
        // 2
        currChecksum.reset();
        currChecksum.update(ByteBufferUtil.toByteBuffer(2));
        sum += currChecksum.getValue();
        refChecksum.update(ByteBufferUtil.toByteBuffer(sum));

        Assert.assertEquals("Circular dependency with field and unordered container checksum.",
                            refChecksum.getValue(), checksum.getValue());
    }

    @Test
    public void testPrimitiveArray() {
        int[] a = {1, 2, 3, 4, 5};

        checksum.update(a);

        refChecksum.update(ByteBufferUtil.toByteBuffer(0)); // a
        for (int e : a) {
            refChecksum.update(ByteBufferUtil.toByteBuffer(e)); // each element in a
        }

        Assert.assertEquals("Primitive array checksum.",
                refChecksum.getValue(), checksum.getValue());
    }

    @Test
    public void test2DArray() {
        int[][] a = { {1, 2, 3}, {4, 5, 6}, {7, 8, 9}};

        checksum.update(a);

        int uid = 0;
        refChecksum.update(ByteBufferUtil.toByteBuffer(uid++)); // a
        for (int[] row : a) {
            refChecksum.update(ByteBufferUtil.toByteBuffer(uid++)); // each array in a
            for (int e : row) {
                refChecksum.update(ByteBufferUtil.toByteBuffer(e)); // each element in row
            }
        }

        Assert.assertEquals("2D array checksum.",
                refChecksum.getValue(), checksum.getValue());
    }

    @Test
    public void testObjectArray() {
        D[] ds = new D[5];

        int uid = 0;
        refChecksum.update(ByteBufferUtil.toByteBuffer(uid++)); // ds
        for (int i = 0; i < ds.length; ++i) {
            F f = new F();
            ds[i] = new D(f);
            refChecksum.update(ByteBufferUtil.toByteBuffer(uid++)); // ds -> ds[i]
            refChecksum.update(ByteBufferUtil.toByteBuffer(ds[i].di)); // ds[i].di
            refChecksum.update(ByteBufferUtil.toByteBuffer(uid++)); // ds[i] -> ds[i].df
            refChecksum.update(ByteBufferUtil.toByteBuffer(ds[i].df.fi)); // ds[i].df.fi
        }
        checksum.update(ds);

        Assert.assertEquals("Object array checksum.",
                refChecksum.getValue(), checksum.getValue());
    }

    @Test
    public void testObjectWithArrayField() {
        ClassWitArrayField cwaf = new ClassWitArrayField();
        checksum.update(cwaf);

        int uid = 0;
        refChecksum.update(ByteBufferUtil.toByteBuffer(uid++)); // cwaf
        refChecksum.update(ByteBufferUtil.toByteBuffer(uid++)); // cwaf -> cwaf.fs
        for (int i = 0; i < cwaf.fs.length; ++i) {
            F f = cwaf.fs[i];
            refChecksum.update(ByteBufferUtil.toByteBuffer(uid++)); // cwaf.fs -> cwaf.fs[i]
            refChecksum.update(ByteBufferUtil.toByteBuffer(f.fi)); // cwaf.fs[i].fi
        }

        Assert.assertEquals("Object with array field checksum.",
                refChecksum.getValue(), checksum.getValue());
    }

    @Test
    public void testList() {
        List<Integer> l = Arrays.asList(1, 2, 3, 4 ,5 ,6);
        checksum.update(l);

        refChecksum.update(ByteBufferUtil.toByteBuffer(0)); // l
        for (int e : l) {
            refChecksum.update(ByteBufferUtil.toByteBuffer(e)); // each int in l
        }

        Assert.assertEquals("List checksum.",
                refChecksum.getValue(), checksum.getValue());
    }

    @Test
    public void testListAsField() {
        ClassWithCollectionField cwcf = new ClassWithCollectionField();
        checksum.update(cwcf);

        int uid = 0;
        refChecksum.update(ByteBufferUtil.toByteBuffer(uid++)); // cwcf
        refChecksum.update(ByteBufferUtil.toByteBuffer(uid++)); // cwcf -> cwcf.fs
        for (int i = 0; i < cwcf.fs.size(); ++i) {
            F f = cwcf.fs.get(i);
            refChecksum.update(ByteBufferUtil.toByteBuffer(uid++)); // cwcf.fs -> cwcf.fs[i]
            refChecksum.update(ByteBufferUtil.toByteBuffer(f.fi)); // cwcf.fs[i].fi
        }

        Assert.assertEquals("List as field checksum.",
                refChecksum.getValue(), checksum.getValue());
    }

    @Test
    public void testListOfList() {
        List<List<Integer>> l = List.of(
                List.of(1),
                List.of(2, 3),
                List.of(4, 5, 6)
        );

        checksum.update(l);

        refChecksum.update(0); // l
        refChecksum.update(1); // l -> l[0]
        refChecksum.update(1); // l[0][0], i.e., 1
        refChecksum.update(2); // l -> l[1]
        refChecksum.update(2); // l[1][0], i.e., 2
        refChecksum.update(3); // l[1][1], i.e., 3
        refChecksum.update(3); // l -> l[2]
        refChecksum.update(4); // l[2][0], i.e., 4
        refChecksum.update(5); // l[2][1], i.e., 5
        refChecksum.update(6); // l[2][2], i.e., 6
    }

    @Test
    public void testUnorderedSet() {
        Set<Integer> s = new HashSet<>(List.of(1, 2, 3));
        checksum.update(s);

        refChecksum.update(ByteBufferUtil.toByteBuffer(0)); // s
        long sum = 0;
        for (int e : s) {
            Checksum currCheckSum = WrappedChecksum.newChecksum();
            currCheckSum.update(ByteBufferUtil.toByteBuffer(e));
            sum += currCheckSum.getValue();
        }
        refChecksum.update(ByteBufferUtil.toByteBuffer(sum));

        Assert.assertEquals("UnorderedSet checksum.",
                refChecksum.getValue(), checksum.getValue());
    }

    @Test
    public void testUnorderedNestedSet() {
        Set<Object> s = new HashSet<>();
        s.add(1);
        F f1 = new F();
        s.add(f1);
        Set<Object> nestedS = new HashSet<>();
        F f2 = new F();
        nestedS.add(f2);
        nestedS.add(2);
        s.add(nestedS);

        checksum.update(s);

        refChecksum.update(ByteBufferUtil.toByteBuffer(0)); // s
        long sum = 0;
        // 1
        Checksum currCheckSum = WrappedChecksum.newChecksum();
        currCheckSum.update(ByteBufferUtil.toByteBuffer(1)); // 1
        sum += currCheckSum.getValue();
        // // f1
        currCheckSum.reset();
        currCheckSum.update(ByteBufferUtil.toByteBuffer(0)); // f1, recounting uid from 0
        currCheckSum.update(ByteBufferUtil.toByteBuffer(f1.fi)); // f1.fi
        sum += currCheckSum.getValue();
        // nestedS ({f2, 2})
        currCheckSum.reset();
        currCheckSum.update(ByteBufferUtil.toByteBuffer(0)); // nestedS, recounting uid from 0

        // hash of nestedS itself
        long nestedSum = 0;
        // f2
        Checksum nestedChecksum = WrappedChecksum.newChecksum();
        nestedChecksum.update(ByteBufferUtil.toByteBuffer(0)); // f2, recounting uid from 0
        nestedChecksum.update(ByteBufferUtil.toByteBuffer(f2.fi)); // f2.fi
        nestedSum += nestedChecksum.getValue();
        // 2
        nestedChecksum.reset();
        nestedChecksum.update(ByteBufferUtil.toByteBuffer(2)); // 2
        nestedSum += nestedChecksum.getValue();

        currCheckSum.update(ByteBufferUtil.toByteBuffer(nestedSum));
        sum += currCheckSum.getValue();

        refChecksum.update(ByteBufferUtil.toByteBuffer(sum));

        Assert.assertEquals("UnorderedNestedSet checksum.",
                            refChecksum.getValue(), checksum.getValue());
    }

    @Test
    public void testUnorderedMap() {
        Map<Integer, F> m = new HashMap<>();
        m.put(2, new F());
        m.put(1, new F());
        m.put(3, new F());
        checksum.update(m);

        refChecksum.update(ByteBufferUtil.toByteBuffer(0)); // m
        long sum = 0;
        for (Map.Entry<Integer, F> e : m.entrySet()) {
            Checksum currChecksum = WrappedChecksum.newChecksum();
            currChecksum.update(ByteBufferUtil.toByteBuffer(0)); // m -> e
            int k = e.getKey();
            F v = e.getValue();
            currChecksum.update(ByteBufferUtil.toByteBuffer(k));
            currChecksum.update(ByteBufferUtil.toByteBuffer(1)); // m -> v
            currChecksum.update(ByteBufferUtil.toByteBuffer(v.fi)); // v.fi
            sum += currChecksum.getValue();
        }
        refChecksum.update(ByteBufferUtil.toByteBuffer(sum));

        Assert.assertEquals("OrderedMap checksum.",
                            refChecksum.getValue(), checksum.getValue());
    }

    @Test
    public void testOrderedMap() {
        Map<Integer, F> m = new TreeMap<>();
        m.put(2, new F());
        m.put(3, new F());
        m.put(1, new F());
        checksum.update(m);

        int uid = 0;
        refChecksum.update(ByteBufferUtil.toByteBuffer(uid++)); // m
        for (Map.Entry<Integer, F> e : m.entrySet()) {
            refChecksum.update(ByteBufferUtil.toByteBuffer(uid++)); // m -> e
            int k = e.getKey();
            F v = e.getValue();
            refChecksum.update(ByteBufferUtil.toByteBuffer(k));
            refChecksum.update(ByteBufferUtil.toByteBuffer(uid++)); // e -> v
            refChecksum.update(ByteBufferUtil.toByteBuffer(v.fi)); // v.fi
        }

        Assert.assertEquals("OrderedMap checksum.",
                refChecksum.getValue(), checksum.getValue());
    }

    @Test
    public void testObjectThatIsIgnoredClass() {
        // Make sure ignore is turned on.
        checksum = new WrappedChecksum(true);

        Exception e = new RuntimeException("Warning: Lightning storm created.");
        checksum.update(e);

        refChecksum.update(ByteBufferUtil.toByteBuffer(0)); // e

        Assert.assertEquals("Ignored class checksum.",
                refChecksum.getValue(), checksum.getValue());
    }

    @Test
    public void testObjectWithIgnoredClassField() {
        // Make sure ignore is turned on.
        checksum = new WrappedChecksum(true);

        ClassWithIgnoredClassField cwice = new ClassWithIgnoredClassField();
        checksum.update(cwice);

        refChecksum.update(ByteBufferUtil.toByteBuffer(0)); // cwice
        refChecksum.update(ByteBufferUtil.toByteBuffer(cwice.i)); // cwice.i
        refChecksum.update(ByteBufferUtil.toByteBuffer(1)); // cwice -> cwice.e

        Assert.assertEquals("Object with ignored class field checksum.",
                refChecksum.getValue(), checksum.getValue());
    }

    @Test
    public void testObjectWithIgnoredClassArrayField() {
        // Make sure ignore is turned on.
        checksum = new WrappedChecksum(true);

        ClassWithIgnoredClassArrayField cwicaf = new ClassWithIgnoredClassArrayField();
        checksum.update(cwicaf);

        int uid = 0;
        refChecksum.update(ByteBufferUtil.toByteBuffer(uid++)); // cwicaf
        refChecksum.update(ByteBufferUtil.toByteBuffer(uid++)); // cwicaf -> cwicaf.es
        for (int i = 0; i < cwicaf.es.length; ++i) {
            Exception e = cwicaf.es[i];
            refChecksum.update(ByteBufferUtil.toByteBuffer(uid++)); // cwicaf.es -> cwicaf.es[i]
        }

        Assert.assertEquals("Object with ignored class field checksum.",
                refChecksum.getValue(), checksum.getValue());
    }

    @Test
    public void testSubclassOfIgnoredClass() {
        // Make sure ignore is turned on.
        checksum = new WrappedChecksum(true);

        SubException se = new SubException();
        checksum.update(se);

        refChecksum.update(ByteBufferUtil.toByteBuffer(0)); // se
        refChecksum.update(ByteBufferUtil.toByteBuffer(se.i)); // se.i

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

    private static class ClassWithListOrSet {}

    private static class ClassWithListOfObject extends ClassWithListOrSet {
        List<Object> list;
    }

    private static class ClassWithSetOfObject extends ClassWithListOrSet {
        Set<Object> set;
    }

    private static class ClassWithRefToParent {
        ClassWithListOrSet parent;
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

    private static class J {
        K jk;
        M jm;
    }

    private static class K {
        int ki = 6;
        J kj;
    }

    private static class M {
        int mi = 7;
        J mj;
    }
}
