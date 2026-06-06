package com.sudoku.model;

/**
 * Immutable record of a single player edit, used for undo on {@link com.sudoku.model.stack.IStack}.
 * <p>
 * When the player changes a cell, {@link Game#applyPlayerMove(int, int, Integer)} pushes
 * a {@code Move} with the previous and new values. {@link Game#undoLastMove()} pops the stack
 * and restores {@link #previousValue}.
 * </p>
 */
public class Move {

    private final Position position;
    private final Integer previousValue;
    private final Integer newValue;

    /**
     * @param position      cell that was edited
     * @param previousValue value before the edit ({@code null} if the cell was empty)
     * @param newValue      value after the edit ({@code null} if cleared)
     */
    public Move(Position position, Integer previousValue, Integer newValue) {
        this.position = position;
        this.previousValue = previousValue;
        this.newValue = newValue;
    }

    /**
     * @return coordinates of the edited cell
     */
    public Position getPosition() {
        return position;
    }

    /**
     * @return value to restore when undoing this move
     */
    public Integer getPreviousValue() {
        return previousValue;
    }

    /**
     * @return value that was applied by this move (for logging or redo extensions)
     */
    public Integer getNewValue() {
        return newValue;
    }
}
