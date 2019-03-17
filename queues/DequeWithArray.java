/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class DequeWithArray<Item> implements Iterable<Item> {
    private Object[] items;
    private int head;
    private int tail;
    private int size = 0;

    // construct an empty deque
    public DequeWithArray() {
        items = new Object[10];
        head = items.length / 2;
        tail = head;
    }

    // is the deque empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // return the number of items on the deque
    public int size() {
        return size;
    }

    // add the item to the front
    public void addFirst(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }

        if (size >= items.length / 2 - 1) {
            resize(items.length * 2);
        }

        if (size == 0) {
            items[head] = item;
        }
        else {
            items[--head] = item;
        }

        size++;
    }

    // add the item to the end
    public void addLast(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }

        if (tail >= size - 1 || size >= items.length / 2 - 1) {
            resize(items.length * 2);
        }

        if (size == 0) {
            items[tail] = item;
        }
        else {
            items[++tail] = item;
        }

        size++;
    }

    // remove and return the item from the front
    public Item removeFirst() {
        if (size == 0) {
            throw new NoSuchElementException();
        }

        if (size < items.length / 4) {
            resize(items.length / 2);
        }

        Item value = (Item) items[head];

        items[head] = null;

        head++;
        if (head > tail) {
            assert size == 0;
            head = items.length / 2;
            tail = head;
        }

        size--;

        return value;
    }

    // remove and return the item from the end
    public Item removeLast() {
        if (size == 0) {
            throw new NoSuchElementException();
        }

        if (size < items.length / 4) {
            resize(items.length / 2);
        }

        Item value = (Item) items[tail];

        items[tail] = null;

        tail--;
        size--;

        if (tail < head) {
            assert size == 0;
            head = items.length / 2;
            tail = head;
        }

        return value;
    }

    // return an iterator over items in order from front to end
    public Iterator<Item> iterator() {
        return new DequeIterator(this);
    }

    private void resize(int newSize) {
        Object[] newItems = new Object[newSize];

        int start = (newSize - size) / 2;

        int originalHead = head;
        head = start;

        for (int i = 0; i < size; i++) {
            tail = start;
            newItems[start] = items[originalHead + i];
            start++;
        }

        items = newItems;
    }

    private class DequeIterator implements Iterator<Item> {
        private final DequeWithArray<Item> deque;
        private int index;

        public DequeIterator(DequeWithArray<Item> deque) {
            this.deque = deque;
            this.index = deque.head;
        }

        @Override
        public boolean hasNext() {
            return index <= tail;
        }

        @Override
        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Item next = (Item) deque.items[index++];
            return next;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    // unit testing (optional)
    public static void main(String[] args) {
        DequeWithArray<Integer> deque = new DequeWithArray<>();

        deque.addLast(1);
        deque.addLast(2);
        StdOut.println(deque.removeFirst());
        deque.addLast(4);
        deque.addLast(5);
        deque.addLast(6);
        StdOut.println(deque.removeFirst());
        deque.addLast(8);

        for (int x : deque) {
            StdOut.println(x);
        }
    }
}
