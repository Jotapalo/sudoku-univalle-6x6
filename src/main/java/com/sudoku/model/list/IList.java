package com.sudoku.model.list;

/**
 * Contract for a generic ordered list with indexed access.
 * <p>
 * Implementations must preserve insertion order. Indices are zero-based.
 * This interface is used by {@link com.sudoku.model.Board} to track empty cells
 * and cells that fail validation without relying on {@code java.util.List}.
 * </p>
 *
 * @param <T> element type stored in the list
 */
public interface IList<T> {

    /**
     * Appends an element at the end of the list.
     *
     * @param item element to append; may be {@code null} if the implementation allows it
     */
    void add(T item);

    /**
     * Inserts an element at the given index, shifting existing elements to the right.
     *
     * @param index position in {@code [0, size()]} (inclusive of {@code size()} for append-at-end semantics)
     * @param item  element to insert
     * @throws IndexOutOfBoundsException if {@code index} is negative or greater than {@link #size()}
     */
    void add(int index, T item);

    /**
     * Removes and returns the element at the given index.
     *
     * @param index position in {@code [0, size())}
     * @return the removed element
     * @throws IndexOutOfBoundsException if {@code index} is out of range
     */
    T remove(int index);

    /**
     * Removes the first occurrence of {@code item} (equality via {@link Object#equals(Object)}).
     *
     * @param item element to remove
     * @return {@code true} if an element was removed, {@code false} if not found
     */
    boolean remove(T item);

    /**
     * Returns the element at the given index without removing it.
     *
     * @param index position in {@code [0, size())}
     * @return element at that index
     * @throws IndexOutOfBoundsException if {@code index} is out of range
     */
    T get(int index);

    /**
     * @return number of elements currently stored
     */
    int size();

    /**
     * @return {@code true} if {@link #size()} is zero
     */
    boolean isEmpty();

    /**
     * Reports whether any element equals {@code item} ({@link Object#equals(Object)}).
     *
     * @param item value to search for
     * @return {@code true} if at least one matching element exists
     */
    boolean contains(T item);

    /**
     * Removes all elements; after this call {@link #isEmpty()} is {@code true}.
     */
    void clear();
}
