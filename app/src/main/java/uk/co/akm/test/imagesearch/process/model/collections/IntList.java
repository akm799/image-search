package uk.co.akm.test.imagesearch.process.model.collections;

public interface IntList extends IntCollection {

    int get(int index);

    int indexOf(int value);

    int lastIndexOf(int value);

    void set(int index, int value);

    int[] toArray();
}
