package com.sudoku.model.stack;

/**
 * Contract for a last-in-first-out (LIFO) stack.
 * <p>
 * In Sudoku36, {@link com.sudoku.model.Game} uses a stack of {@link com.sudoku.model.Move}
 * objects so player edits can be undone in reverse chronological order.
 * </p>
 *
 * @param <T> element type stored on the stack
 */
public interface IStack<T> {

    /**
     * Pushes {@code item} onto the top of the stack.
     *
     * @param item element to push
     */
    void push(T item);

    /**
     * Removes and returns the top element.
     *
     * @return the element that was on top
     * @throws IllegalStateException if the stack is empty
     */
    T pop();

    /**
     * Returns the top element without removing it.
     *
     * @return current top element
     * @throws IllegalStateException if the stack is empty
     */
    T peek();

    /**
     * @return {@code true} if the stack contains no elements
     */
    boolean isEmpty();

    /**
     * @return number of elements on the stack
     */
    int size();

    /**
     * Discards all elements.
     */
    void clear();
}
