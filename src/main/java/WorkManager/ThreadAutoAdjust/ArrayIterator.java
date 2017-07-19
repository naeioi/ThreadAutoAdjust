package WorkManager.ThreadAutoAdjust;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class ArrayIterator implements Iterator {
    private final Object array[];
    private final int maxIndex;
    private int index;

    public ArrayIterator(Object aobj[]) {
        this(aobj, 0, aobj.length);
    }

    public ArrayIterator(Object aobj[], int i, int j) {
        if (i < 0) {
            throw new IllegalArgumentException();
        }
        if (i > aobj.length) {
            throw new IllegalArgumentException();
        }
        if (j > aobj.length - i) {
            throw new IllegalArgumentException();
        } else {
            array = aobj;
            index = i;
            maxIndex = j + i;
            return;
        }
    }

    public boolean hasNext() {
        return index < maxIndex;
    }

    public Object next() {
        if (index >= maxIndex) {
            throw new NoSuchElementException();
        } else {
            return array[index++];
        }
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
