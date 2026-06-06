package com.sudoku.model.dequeue;

/**
 * Contract for a double-ended queue (deque): elements can be added or removed from both ends.
 * <p>
 * Provided as a course-required data structure. The current Sudoku36 gameplay path does not
 * depend on this deque, but the type is available for extensions or assignments.
 * </p>
 *
 * @param <T> element type stored in the deque
 */
public interface IDequeue<T> {

    /**
     * Inserts {@code item} at the head (front) of the deque.
     *
     * @param item element to add
     */
    void addFirst(T item);

    /**
     * Inserts {@code item} at the tail (rear) of the deque.
     *
     * @param item element to add
     */
    void addLast(T item);

    /**
     * Removes and returns the element at the head.
     *
     * @return front element
     * @throws IllegalStateException if the deque is empty
     */
    T removeFirst();

    /**
     * Removes and returns the element at the tail.
     *
     * @return rear element
     * @throws IllegalStateException if the deque is empty
     */
    T removeLast();

    /**
     * Returns the head element without removing it.
     *
     * @return front element
     * @throws IllegalStateException if the deque is empty
     */
    T peekFirst();

    /**
     * Returns the tail element without removing it.
     *
     * @return rear element
     * @throws IllegalStateException if the deque is empty
     */
    T peekLast();

    /**
     * @return {@code true} if the deque has no elements
     */
    boolean isEmpty();

    /**
     * @return number of elements in the deque
     */
    int size();

    /**
     * Removes all elements.
     */
    void clear();
}
