/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private Object[] items;
    private int size;
    private int tail;

    // construct an empty randomized queue
    public RandomizedQueue() {
        items = new Object[10];
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return size;
    }

    // add the item
    public void enqueue(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }

        if (size >= items.length / 2) {
            resize(items.length * 2);
        }

        items[tail++] = item;
        size++;
    }

    // remove and return a random item
    public Item dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        if (size <= items.length / 4) {
            resize(items.length / 2);
        }

        int index = StdRandom.uniform(size);
        Item item = (Item) items[index];
        items[index] = items[size - 1];

        size--;
        return item;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        int index = StdRandom.uniform(size);
        Item item = (Item) items[index];

        return item;
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new RandomizedQueueIterator(this);
    }

    private void resize(int newSize) {
        Object[] newItems = new Object[newSize];
        int j = 0;
        for (int i = 0; j < size && i < items.length; i++) {
            Object item = items[i];
            if (item != null) {
                newItems[j++] = item;
            }
        }

        tail = j;
        items = newItems;
    }

    private class RandomizedQueueIterator implements Iterator<Item> {
        private final RandomizedQueue<Item> queue;
        private final int[] indices;
        private int counter;

        public RandomizedQueueIterator(RandomizedQueue<Item> queue) {
            this.queue = queue;

            indices = new int[queue.size()];
            for (int i = 0; i < queue.size; i++) {
                indices[i] = i;
            }
            StdRandom.shuffle(indices);

            counter = indices.length - 1;
        }

        @Override
        public boolean hasNext() {
            return counter >= 0;
        }

        @Override
        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            return (Item) queue.items[indices[counter--]];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    // unit testing (optional)
    public static void main(String[] args) {
        RandomizedQueue<Integer> queue = new RandomizedQueue<>();

        for (int i = 0; i < 100; i++) {
            if (!queue.isEmpty() && StdRandom.uniform(10) > 7) {
                queue.dequeue();
            }
            else {
                queue.enqueue(i);
            }
        }
    }
}
