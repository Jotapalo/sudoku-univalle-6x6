package com.sudoku.model.dequeue;

/**
 * Doubly-linked deque implementing {@link IDequeue}.
 * <p>
 * All operations at either end are O(1). {@code head} points to the front;
 * {@code tail} points to the rear.
 * </p>
 *
 * @param <T> element type stored in the deque
 */
public class Dequeue<T> implements IDequeue<T> {

    /** Node with links to both neighbors. */
    private static class Node<T> {
        T data;
        Node<T> prev;
        Node<T> next;

        Node(T data) {
            this.data = data;
        }
    }

    /** Front of the deque, or {@code null} when empty. */
    private Node<T> head;
    /** Rear of the deque, or {@code null} when empty. */
    private Node<T> tail;
    /** Element count. */
    private int size;

    /**
     * Inserts an element at the front of the deque.
     *
     * @param item element to insert
     */
    @Override
    public void addFirst(T item) {
        Node<T> node = new Node<>(item);
        if (head == null) {
            head = tail = node;
        } else {
            node.next = head;
            head.prev = node;
            head = node;
        }
        size++;
    }

    /**
     * Inserts an element at the back of the deque.
     *
     * @param item element to insert
     */
    @Override
    public void addLast(T item) {
        Node<T> node = new Node<>(item);
        if (tail == null) {
            head = tail = node;
        } else {
            node.prev = tail;
            tail.next = node;
            tail = node;
        }
        size++;
    }

    /**
     * Removes and returns the front element.
     *
     * @return removed front element
     * @throws IllegalStateException if the deque is empty
     */
    @Override
    public T removeFirst() {
        if (isEmpty()) {
            throw new IllegalStateException("Deque is empty");
        }
        T data = head.data;
        head = head.next;
        if (head == null) {
            tail = null;
        } else {
            head.prev = null;
        }
        size--;
        return data;
    }

    /**
     * Removes and returns the rear element.
     *
     * @return removed rear element
     * @throws IllegalStateException if the deque is empty
     */
    @Override
    public T removeLast() {
        if (isEmpty()) {
            throw new IllegalStateException("Deque is empty");
        }
        T data = tail.data;
        tail = tail.prev;
        if (tail == null) {
            head = null;
        } else {
            tail.next = null;
        }
        size--;
        return data;
    }

    /**
     * Returns, but does not remove, the front element.
     *
     * @return front element
     * @throws IllegalStateException if the deque is empty
     */
    @Override
    public T peekFirst() {
        if (isEmpty()) {
            throw new IllegalStateException("Deque is empty");
        }
        return head.data;
    }

    /**
     * Returns, but does not remove, the rear element.
     *
     * @return rear element
     * @throws IllegalStateException if the deque is empty
     */
    @Override
    public T peekLast() {
        if (isEmpty()) {
            throw new IllegalStateException("Deque is empty");
        }
        return tail.data;
    }

    /**
     * Checks whether the deque contains no elements.
     *
     * @return {@code true} if the deque is empty
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the number of elements currently stored in the deque.
     *
     * @return element count
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Removes all elements from the deque.
     */
    @Override
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }
}
