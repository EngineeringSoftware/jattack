package sketchy.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * A unique list data structure implementation, which combines and
 * ArrayList and HashMap as underlying data structures. This list
 * thus supports efficient (constant time) operations by both index
 * and element.
 * <p>
 * @param <E> the type of elements in this list
 */
public class UniqueList<E> extends AbstractList<E> {
    private final List<E> list;
    private final Map<E, Integer> indices; // maps element to index

    public UniqueList() {
        this.list = new ArrayList<>();
        this.indices = new HashMap<>();
    }

    public UniqueList(Collection<? extends E> collection) {
        this.list = new ArrayList<>();
        this.indices = new HashMap<>();
        addAll(collection);
    }

    /**
     * Appends the specified element to the end of this list. Returns
     * false if this element already exists; otherwise returns true.
     */
    @Override
    public boolean add(E e) {
        if (indices.containsKey(e)) {
            return false;
        }
        indices.put(e, list.size());
        list.add(e);
        return true;
    }

    /**
     * Removes an element at the given index from this list. Returns
     * the element removed.
     *
     * TODO: updating indices is O(n)! We want O(1).
     */
    @Override
    public E remove(int index) throws IndexOutOfBoundsException {
        E e = list.remove(index);
        // update indices
        for (int i = index; i < list.size(); i++) {
            indices.put(list.get(i), i);
        }
        indices.remove(e);
        return e;
    }

    /**
     * Removes an element from this list. Returns false if the
     * specified element does not exists otherwise returns true.
     *
     * TODO: remove(idx) is O(n)! We want O(1).
     */
    @Override
    public boolean remove(Object o) {
        if (!indices.containsKey(o)) {
            return false;
        }
        int idx = indices.get(o);
        remove(idx);
        return true;
    }

    /**
     * Note this is a shallow copy. Two collections share the same
     * references.
     */
    @Override
    public boolean addAll(Collection<? extends E> collection) {
        boolean isModified = false;
        for (E e : collection) {
            if (add(e)) {
                isModified = true;
            }
        }
        return isModified;
    }

    @Override
    public E get(int index) throws IndexOutOfBoundsException {
        return list.get(index);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public Iterator<E> iterator() {
        return list.iterator();
    }

    /**
     * Randomly pick one element from the list.
     */
    public E pick(Random random) {
        if (list.isEmpty()) {
            throw new RuntimeException("Nothing to pick since this unique list is empty!");
        }
        return get(random.nextInt(list.size()));
    }

    @Override
    public String toString() {
        return list.toString();
    }
}
