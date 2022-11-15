package jattack.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class UniqueListTest {

    @Test
    public void testAddNew() {
        UniqueList<String> ul = new UniqueList<>(Arrays.asList("a", "b"));
        ul.add("c");
        List<String> expected = Arrays.asList("a", "b", "c");
        Assert.assertTrue("UniqueList is not equal to " + expected,
                assertUniqueListEquals(ul, expected));
    }

    @Test
    public void testAddExisting() {
        UniqueList<String> ul = new UniqueList<>(Arrays.asList("a", "b"));
        ul.add("b");
        List<String> expected = Arrays.asList("a", "b");
        Assert.assertTrue("UniqueList is not equal to " + expected,
                assertUniqueListEquals(ul, expected));
    }

    @Test
    public void testRemoveLast() {
        UniqueList<String> ul = new UniqueList<>(Arrays.asList("a", "b", "c"));
        ul.remove("c");
        List<String> expected = Arrays.asList("a", "b");
        Assert.assertTrue("UniqueList is not equal to " + expected,
                assertUniqueListEquals(ul, expected));
    }

    @Test
    public void testRemoveFirst() {
        UniqueList<String> ul = new UniqueList<>(Arrays.asList("a", "b", "c"));
        ul.remove("a");
        List<String> expected = Arrays.asList("b", "c");
        Assert.assertTrue("UniqueList " + ul + " is not equal to " + expected + ".",
                assertUniqueListEquals(ul, expected));
    }

    @Test
    public void testRemoveMiddle() {
        UniqueList<String> ul = new UniqueList<>(Arrays.asList("a", "b", "c"));
        ul.remove("b");
        List<String> expected = Arrays.asList("a", "c");
        Assert.assertTrue("UniqueList is not equal to " + expected,
                assertUniqueListEquals(ul, expected));
    }

    @Test
    public void testAddRemoveSequence() {
        UniqueList<String> ul = new UniqueList<>();
        ul.add("a");
        ul.add("b");
        ul.remove("a");
        ul.add("b");
        List<String> expected = Arrays.asList("b");
        Assert.assertTrue("UniqueList is not equal to " + expected,
                assertUniqueListEquals(ul, expected));

    }

    private <T> boolean assertUniqueListEquals(
            UniqueList<? extends T> ul1, List<? extends T> ul2) {
        if (ul1.size() != ul2.size()) {
            return false;
        }
        for (int i = 0; i < ul1.size(); i++) {
            if (!ul1.get(i).equals(ul2.get(i))) {
                return false;
            }
        }
        return true;
    }
}
