package com.sudoku.model;

import java.util.Objects;

/**
 * Immutable grid coordinates for a cell on the 6×6 Sudoku board.
 * <p>
 * Rows and columns are zero-based: {@code (0,0)} is the top-left corner,
 * {@code (5,5)} is the bottom-right. Used as keys in {@link com.sudoku.model.tables.ITable}
 * and inside {@link Move} / {@link Hint}.
 * </p>
 */
public class Position {

    /** Zero-based row index in {@code [0, Board.SIZE)}. */
    private final int row;
    /** Zero-based column index in {@code [0, Board.SIZE)}. */
    private final int column;

    /**
     * Creates a position on the board.
     *
     * @param row    row index (0 = top)
     * @param column column index (0 = left)
     */
    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    /**
     * @return zero-based row index
     */
    public int getRow() {
        return row;
    }

    /**
     * @return zero-based column index
     */
    public int getColumn() {
        return column;
    }

    /**
     * Compares this position with another object.
     *
     * @param obj object to compare against
     * @return {@code true} if the other object is a {@code Position} with the same row and column
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Position other)) {
            return false;
        }
        return row == other.row && column == other.column;
    }

    /**
     * Returns a hash code derived from the row and column values.
     *
     * @return hash code for this position
     */
    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

    /**
     * Returns a readable representation of this position.
     *
     * @return coordinates in the form {@code (row, column)}
     */
    @Override
    public String toString() {
        return "(" + row + ", " + column + ")";
    }
}
