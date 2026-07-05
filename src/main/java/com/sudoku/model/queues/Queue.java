package com.sudoku.model.queues;

/**
 * Singly-linked FIFO queue with separate front and rear pointers.
 * <p>
 * Enqueue and dequeue are O(1). Used for the hint pipeline in {@link com.sudoku.model.Game}.
 * </p>
 *
 * @param <T> element type stored in the queue
 */
public class Queue<T> implements IQueue<T> {

    /** Node in the singly-linked chain. */
    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
        }
    }

    /** First node to be dequeued, or {@code null} when empty. */
    private Node<T> front;
    /** Last node in the chain, or {@code null} when empty. */
    private Node<T> rear;
    /** Cached size for O(1) {@link #size()}. */
    private int size;

    /**
     * Inserts an element at the rear of the queue.
     *
     * @param item element to enqueue
     */
    @Override
    public void enqueue(T item) {
        Node<T> node = new Node<>(item);
        if (rear == null) {
            front = rear = node;
        } else {
            rear.next = node;
            rear = node;
        }
        size++;
    }

    /**
     * Removes and returns the front element.
     *
     * @return removed element
     * @throws IllegalStateException if the queue is empty
     */
    @Override
    public T dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }
        T data = front.data;
        front = front.next;
        if (front == null) {
            rear = null;
        }
        size--;
        return data;
    }

    /**
     * Returns, but does not remove, the front element.
     *
     * @return front element
     * @throws IllegalStateException if the queue is empty
     */
    @Override
    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }
        return front.data;
    }

    /**
     * Checks whether the queue contains no elements.
     *
     * @return {@code true} if the queue is empty
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the number of elements currently stored in the queue.
     *
     * @return queue size
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Removes all elements from the queue.
     */
    @Override
    public void clear() {
        front = null;
        rear = null;
        size = 0;
    }
}
