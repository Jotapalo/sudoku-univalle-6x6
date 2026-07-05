package com.sudoku.model.stack;

/**
 * Linked-list implementation of {@link IStack} with O(1) push, pop, and peek.
 *
 * @param <T> element type stored on the stack
 */
public class Stack<T> implements IStack<T> {

    /** Node storing one stack entry and the link to the element below. */
    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
        }
    }

    /** Top of the stack, or {@code null} when empty. */
    private Node<T> top;
    /** Number of elements currently on the stack. */
    private int size;

    /**
     * Pushes an element onto the top of the stack.
     *
     * @param item element to push
     */
    @Override
    public void push(T item) {
        Node<T> node = new Node<>(item);
        node.next = top;
        top = node;
        size++;
    }

    /**
     * Removes and returns the top element.
     *
     * @return removed element
     * @throws IllegalStateException if the stack is empty
     */
    @Override
    public T pop() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }
        T data = top.data;
        top = top.next;
        size--;
        return data;
    }

    /**
     * Returns, but does not remove, the top element.
     *
     * @return top element
     * @throws IllegalStateException if the stack is empty
     */
    @Override
    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }
        return top.data;
    }

    /**
     * Checks whether the stack contains no elements.
     *
     * @return {@code true} if the stack is empty
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the number of elements currently on the stack.
     *
     * @return stack size
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Removes all elements from the stack.
     */
    @Override
    public void clear() {
        top = null;
        size = 0;
    }
}
