package uk.co.akm.test.imagesearch.process.model.collections;

/**
 * Created by Thanos Mavroidis on 07/04/2019.
 */
final class EmptyIntCollection implements IntCollection {

    EmptyIntCollection() {}

    private static final IntIterator EMPTY_ITERATOR = new IntIterator() {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public int next() {
            throw new RuntimeException("This empty iterator instance does not contain any values.");
        }
    };

    @Override
    public void add(int value) {
        throw new RuntimeException("Cannot change this immutable empty IntCollection instance.");
    }

    @Override
    public void addAll(IntCollection intCollection) {
        throw new RuntimeException("Cannot change this immutable empty IntCollection instance.");
    }

    @Override
    public void clear() {}

    @Override
    public boolean contains(int value) {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public IntIterator iterator() {
        return EMPTY_ITERATOR;
    }

    @Override
    public int size() {
        return 0;
    }
}
