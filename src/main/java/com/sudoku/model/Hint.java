package com.sudoku.model;

/**
 * A single help suggestion: one empty cell and the correct digit from the hidden solution.
 * <p>
 * Hints are precomputed at game start and stored in {@link com.sudoku.model.queues.IQueue}.
 * Applying a hint does not count as a player move on the undo stack.
 * </p>
 */
public class Hint {

    private final Position position;
    private final int suggestedValue;

    /**
     * @param position        target cell (must be empty when the hint is applied)
     * @param suggestedValue  correct digit from the full solution (1–6)
     */
    public Hint(Position position, int suggestedValue) {
        this.position = position;
        this.suggestedValue = suggestedValue;
    }

    /**
     * @return cell that will receive the suggested value
     */
    public Position getPosition() {
        return position;
    }

    /**
     * @return digit to place (from the board's stored solution grid)
     */
    public int getSuggestedValue() {
        return suggestedValue;
    }
}
