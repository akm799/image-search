package uk.co.akm.test.imagesearch.process.model.collections;

import java.util.NoSuchElementException;

public final class IntArrayList implements IntList {
    private static final int DEFAULT_INITIAL_CAPACITY = 1024;
    private static final int DEFAULT_CAPACITY_INCREMENT = 512;

    private static int checkAndGetSize(IntCollection intCollection) {
        if (intCollection == null) {
            throw new IllegalArgumentException("Null input IntCollection constructor argument is not allowed.");
        } else {
            return intCollection.size();
        }
    }

    private final int capacityIncrement;

    private int size;
    private int[] values;

    public IntArrayList() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    public IntArrayList(int initialCapacity) {
        this(initialCapacity, DEFAULT_CAPACITY_INCREMENT);
    }

    public IntArrayList(int initialCapacity, int capacityIncrement) {
        checkArguments(initialCapacity, capacityIncrement);

        this.values = new int[initialCapacity];
        this.capacityIncrement = capacityIncrement;
    }

    public IntArrayList(IntCollection intCollection) {
        this(checkAndGetSize(intCollection));

        addAll(intCollection);
    }

    private void checkArguments(int initialCapacity, int capacityIncrement) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("'initialCapacity' argument (" + initialCapacity + ") cannot be less than 1.");
        }

        if (capacityIncrement < 1) {
            throw new IllegalArgumentException("'capacityIncrement' argument (" + capacityIncrement + ") cannot be less than 1.");
        }
    }

    @Override
    public void add(int value) {
        ensureCapacity();
        values[size++] = value;
    }

    @Override
    public void addAll(IntCollection intCollection) {
        if (intCollection != null) {
            final IntIterator iterator = intCollection.iterator();
            if (iterator != null) {
                while (iterator.hasNext()) {
                    add(iterator.next());
                }
            }
        }
    }

    @Override
    public void clear() {
        size = 0;
    }

    @Override
    public boolean contains(int value) {
        for (int i=0 ; i<size ; i++) {
            if (values[i] == value) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int get(int index) {
        checkIndex(index);

        return values[index];
    }

    @Override
    public int indexOf(int value) {
        for (int i=0 ; i<size ; i++) {
            if (values[i] == value) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public int lastIndexOf(int value) {
        for (int i=size-1 ; i>=0 ; i--) {
            if (values[i] == value) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public boolean isEmpty() {
        return (size == 0);
    }

    @Override
    public IntIterator iterator() {
        return new LocalIntIterator(size, values);
    }

    @Override
    public void set(int index, int value) {
        checkIndex(index);
        values[index] = value;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int[] toArray() {
        final int[] array = new int[size];
        System.arraycopy(values, 0, array, 0, size);

        return array;
    }

    private void checkIndex(int index) {
        if (size == 0) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds: IntArrayList is empty.");
        }

        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds: [0, " + size + ").");
        }
    }

    private void ensureCapacity() {
        if (size == values.length) {
            final int[] newValues = new int[values.length + capacityIncrement];
            System.arraycopy(values, 0, newValues, 0, size);
            values = newValues;
        }
    }

    private static final class LocalIntIterator implements IntIterator {
        private final int size;
        private final int[] values;

        private int index;

        private LocalIntIterator(int size, int[] values) {
            this.size = size;
            this.values = values;
        }

        @Override
        public boolean hasNext() {
            return (index < size);
        }

        @Override
        public int next() {
            if (index == size) {
                throw new NoSuchElementException();
            }

            return values[index++];
        }
    }
}
