package WorkManager.ThreadAutoAdjust;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class ArrayIterator implements Iterator {
    private final Object array[];
    private final int maxIndex;
    private int index;

    public ArrayIterator(Object array[]) {
        this(array, 0, array.length);
    }

    public ArrayIterator(Object[] array, int startIndex, int count) {
        if (startIndex < 0) {
            throw new IllegalArgumentException();
        }
        if (startIndex > array.length) {
            throw new IllegalArgumentException();
        }
        if (count > array.length - startIndex) {
            throw new IllegalArgumentException();
        } else {
            this.array = array;
            this.index = startIndex;
            this.maxIndex = count + startIndex;
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
