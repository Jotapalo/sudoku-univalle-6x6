package com.sudoku.model;

/**
 * One cell on the 6×6 Sudoku grid, including player-editable state and UI flags.
 * <p>
 * A cell may hold a value from 1 to 6, or {@code null} when empty. {@link #given}
 * marks puzzle clues that cannot be edited. {@link #invalid} is set by
 * {@link Board#recomputeValidation()} when the value duplicates another in the same
 * row, column, or 2×3 block. {@link #hinted} marks values filled by the help system.
 * </p>
 */
public class Cell {

    /** Fixed coordinates of this cell on the board. */
    private final Position position;
    /** Current digit (1–6) or {@code null} if empty. */
    private Integer value;
    /** {@code true} if this cell was part of the generated puzzle (not editable). */
    private boolean given;
    /** {@code true} if the current value conflicts with Sudoku rules. */
    private boolean invalid;
    /** {@code true} if the value was applied via {@link Board#applyHint(int, int, int)}. */
    private boolean hinted;

    /**
     * Creates an empty, editable cell at {@code position}.
     *
     * @param position board coordinates
     */
    public Cell(Position position) {
        this.position = position;
    }

    /**
     * @return immutable position of this cell
     */
    public Position getPosition() {
        return position;
    }

    /**
     * @return current value, or {@code null} when the cell is empty
     */
    public Integer getValue() {
        return value;
    }

    /**
     * Sets the displayed value. Does not update validation or empty-cell lists;
     * callers should use {@link Board#setPlayerValue} or {@link Board#applyHint}.
     *
     * @param value digit 1–6, or {@code null} to clear
     */
    public void setValue(Integer value) {
        this.value = value;
    }

    /**
     * @return {@code true} if this is a fixed clue from the puzzle generator
     */
    public boolean isGiven() {
        return given;
    }

    /**
     * @param given whether the cell is a non-editable clue
     */
    public void setGiven(boolean given) {
        this.given = given;
    }

    /**
     * @return {@code true} if the cell participates in a duplicate row/column/block conflict
     */
    public boolean isInvalid() {
        return invalid;
    }

    /**
     * @param invalid validation flag (typically set only by {@link Board})
     */
    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }

    /**
     * @return {@code true} if the value came from a hint, not manual entry
     */
    public boolean isHinted() {
        return hinted;
    }

    /**
     * @param hinted hint flag; cleared when the player edits the cell
     */
    public void setHinted(boolean hinted) {
        this.hinted = hinted;
    }

    /**
     * @return {@code true} if {@link #getValue()} is {@code null}
     */
    public boolean isEmpty() {
        return value == null;
    }
}
