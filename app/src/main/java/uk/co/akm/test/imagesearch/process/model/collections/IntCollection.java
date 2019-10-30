package uk.co.akm.test.imagesearch.process.model.collections;

public interface IntCollection {
    IntCollection EMPTY = new EmptyIntCollection();

    void add(int value);

    void addAll(IntCollection intCollection);

    void clear();

    boolean contains(int value);

    boolean isEmpty();

    IntIterator iterator();

    int size();
}