package com.sudoku.model.tables;

/**
 * Contract for a mutable key–value map with unique keys.
 * <p>
 * {@link com.sudoku.model.Board} maps each {@link com.sudoku.model.Position} to its
 * {@link com.sudoku.model.Cell} for constant-time lookup by coordinates.
 * </p>
 *
 * @param <K> key type (must support consistent {@link Object#equals(Object)} / {@link Object#hashCode()})
 * @param <V> value type
 */
public interface ITable<K, V> {

    /**
     * Associates {@code key} with {@code value}, replacing any previous value for that key.
     *
     * @param key   lookup key
     * @param value value to store
     */
    void put(K key, V value);

    /**
     * Returns the value associated with {@code key}, or {@code null} if absent.
     *
     * @param key lookup key
     * @return stored value, or {@code null} if not found
     */
    V get(K key);

    /**
     * Removes the mapping for {@code key} if present.
     *
     * @param key lookup key
     * @return previous value, or {@code null} if the key was not mapped
     */
    V remove(K key);

    /**
     * @param key lookup key
     * @return {@code true} if a mapping exists for {@code key}
     */
    boolean containsKey(K key);

    /**
     * @return {@code true} if the table has no entries
     */
    boolean isEmpty();

    /**
     * @return number of key–value pairs stored
     */
    int size();

    /**
     * Removes all mappings.
     */
    void clear();
}
