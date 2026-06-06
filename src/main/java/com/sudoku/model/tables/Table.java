package com.sudoku.model.tables;

/**
 * Open-addressing-style table backed by a dynamic array of key–value entries.
 * <p>
 * Lookup scans the array linearly (O(n) worst case, acceptable for 36 cells).
 * When the backing array is full, capacity doubles. Updating an existing key
 * overwrites the value in place without increasing {@link #size()}.
 * </p>
 *
 * @param <K> key type
 * @param <V> value type
 */
public class Table<K, V> implements ITable<K, V> {

    /** Single slot in the backing store. */
    private static class Entry<K, V> {
        final K key;
        V value;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    /** Initial capacity of the internal array. */
    private static final int INITIAL_CAPACITY = 16;

    /** Sparse array of entries; indices {@code [0, size)} are in use. */
    private Entry<K, V>[] entries;
    /** Number of active entries. */
    private int size;

    /**
     * Creates an empty table with default initial capacity.
     */
    @SuppressWarnings("unchecked")
    public Table() {
        entries = new Entry[INITIAL_CAPACITY];
    }

    @Override
    public void put(K key, V value) {
        int index = findIndex(key);
        if (index >= 0) {
            entries[index].value = value;
            return;
        }
        ensureCapacity();
        entries[size++] = new Entry<>(key, value);
    }

    @Override
    public V get(K key) {
        int index = findIndex(key);
        return index >= 0 ? entries[index].value : null;
    }

    @Override
    public V remove(K key) {
        int index = findIndex(key);
        if (index < 0) {
            return null;
        }
        V value = entries[index].value;
        entries[index] = entries[size - 1];
        entries[size - 1] = null;
        size--;
        return value;
    }

    @Override
    public boolean containsKey(K key) {
        return findIndex(key) >= 0;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            entries[i] = null;
        }
        size = 0;
    }

    /**
     * Linear search for the index of {@code key}, or {@code -1} if not found.
     */
    private int findIndex(K key) {
        for (int i = 0; i < size; i++) {
            K entryKey = entries[i].key;
            if (entryKey == null ? key == null : entryKey.equals(key)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Doubles array capacity when {@code size} reaches the current length.
     */
    @SuppressWarnings("unchecked")
    private void ensureCapacity() {
        if (size < entries.length) {
            return;
        }
        Entry<K, V>[] resized = new Entry[entries.length * 2];
        System.arraycopy(entries, 0, resized, 0, size);
        entries = resized;
    }
}
