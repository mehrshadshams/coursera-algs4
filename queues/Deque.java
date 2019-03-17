/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {
    private Node head;
    private Node tail;
    private int size;

    // construct an empty deque
    public Deque() {

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

        Node node = new Node();
        node.value = item;

        if (head == null) {
            head = node;
            tail = node;
        }
        else {
            head.prev = node;
            node.next = head;
            head = node;
        }

        size++;
    }

    // add the item to the end
    public void addLast(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }

        Node node = new Node();
        node.value = item;

        if (tail == null) {
            head = node;
            tail = node;
        }
        else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }

        size++;
    }

    // remove and return the item from the front
    public Item removeFirst() {
        if (size == 0) {
            throw new NoSuchElementException();
        }

        Item value = head.value;

        Node next = head.next;
        if (next != null) {
            next.prev = null;
        }

        head.next = null;
        head = next;
        if (head == null) {
            tail = null;
        }

        size--;

        return value;
    }

    // remove and return the item from the end
    public Item removeLast() {
        if (size == 0) {
            throw new NoSuchElementException();
        }

        Item value = tail.value;
        Node prev = tail.prev;
        if (prev != null) {
            prev.next = null;
        }

        tail.prev = null;
        tail = prev;
        if (tail == null) {
            head = null;
        }
        size--;


        return value;
    }

    // return an iterator over items in order from front to end
    public Iterator<Item> iterator() {
        return new DequeIterator(this);
    }

    private class DequeIterator implements Iterator<Item> {
        private Node current;

        private DequeIterator(Deque<Item> deque) {
            current = deque.head;
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Item value = current.value;
            current = current.next;
            return value;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private class Node {
        private Item value;
        private Node prev;
        private Node next;
    }

    public static void main(String[] args) {
        Deque<Integer> deque = new Deque<>();

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
