package com.sudoku.model.queues;

/**
 * Contract for a first-in-first-out (FIFO) queue.
 * <p>
 * {@link com.sudoku.model.Game} enqueues up to {@link com.sudoku.model.Game#MAX_HINTS}
 * {@link com.sudoku.model.Hint} instances at the start of a round; each call to
 * {@link com.sudoku.model.Game#requestHint()} dequeues one hint in FIFO order.
 * </p>
 *
 * @param <T> element type stored in the queue
 */
public interface IQueue<T> {

    /**
     * Adds {@code item} at the rear of the queue.
     *
     * @param item element to enqueue
     */
    void enqueue(T item);

    /**
     * Removes and returns the element at the front of the queue.
     *
     * @return front element
     * @throws IllegalStateException if the queue is empty
     */
    T dequeue();

    /**
     * Returns the front element without removing it.
     *
     * @return front element
     * @throws IllegalStateException if the queue is empty
     */
    T peek();

    /**
     * @return {@code true} if the queue has no elements
     */
    boolean isEmpty();

    /**
     * @return number of elements in the queue
     */
    int size();

    /**
     * Removes all elements from the queue.
     */
    void clear();
}
