package com.sudoku.model.list;

/**
 * Singly-linked list implementation of {@link IList}.
 * <p>
 * Append-at-tail is O(n) because only a head reference is kept; indexed access is O(n).
 * Suitable for the modest sizes in Sudoku36 (at most 36 cells per list).
 * </p>
 *
 * @param <T> element type stored in the list
 */
public class LinkedList<T> implements IList<T> {

    /**
     * Internal node holding one element and a reference to the next node.
     */
    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
        }
    }

    /** First node in the chain, or {@code null} when the list is empty. */
    private Node<T> head;
    /** Cached element count for O(1) {@link #size()}. */
    private int size;

    @Override
    public void add(T item) {
        Node<T> node = new Node<>(item);
        if (head == null) {
            head = node;
        } else {
            Node<T> current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = node;
        }
        size++;
    }

    @Override
    public void add(int index, T item) {
        validateIndexForInsert(index);
        if (index == 0) {
            Node<T> node = new Node<>(item);
            node.next = head;
            head = node;
        } else {
            Node<T> prev = nodeAt(index - 1);
            Node<T> node = new Node<>(item);
            node.next = prev.next;
            prev.next = node;
        }
        size++;
    }

    @Override
    public T remove(int index) {
        validateIndex(index);
        T removed;
        if (index == 0) {
            removed = head.data;
            head = head.next;
        } else {
            Node<T> prev = nodeAt(index - 1);
            removed = prev.next.data;
            prev.next = prev.next.next;
        }
        size--;
        return removed;
    }

    @Override
    public boolean remove(T item) {
        if (head == null) {
            return false;
        }
        if (head.data != null && head.data.equals(item)) {
            head = head.next;
            size--;
            return true;
        }
        Node<T> current = head;
        while (current.next != null) {
            if (current.next.data != null && current.next.data.equals(item)) {
                current.next = current.next.next;
                size--;
                return true;
            }
            current = current.next;
        }
        return false;
    }

    @Override
    public T get(int index) {
        return nodeAt(index).data;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(T item) {
        Node<T> current = head;
        while (current != null) {
            if (current.data == null ? item == null : current.data.equals(item)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    @Override
    public void clear() {
        head = null;
        size = 0;
    }

    /**
     * Walks the list from the head until the node at {@code index} is reached.
     *
     * @param index zero-based position
     * @return node at that index
     * @throws IndexOutOfBoundsException if {@code index} is invalid
     */
    private Node<T> nodeAt(int index) {
        validateIndex(index);
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current;
    }

    /**
     * Validates an index used for read or remove operations.
     */
    private void validateIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }
    }

    /**
     * Validates an index used for insert operations (allows {@code index == size}).
     */
    private void validateIndexForInsert(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }
    }
}
