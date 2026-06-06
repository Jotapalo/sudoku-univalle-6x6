package com.sudoku.model;

import com.sudoku.model.list.IList;
import com.sudoku.model.stack.IStack;
import com.sudoku.model.stack.Stack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Facade for Sudoku36 game rules: new games, player moves, undo, and limited hints.
 * <p>
 * Owns a {@link Board}, an undo {@link IStack} of {@link Move}s, and generates hints from the
 * current empty cells. Puzzle creation is delegated to {@link PuzzleGenerator}.
 * </p>
 */
public class Game {

    /** Number of given clues placed in each 2×3 block when starting a new game. */
    public static final int GIVENS_PER_BLOCK = 2;
    /** Maximum hints that can be requested in one game. */
    public static final int MAX_HINTS = 10;

    private final Board board = new Board();
    private final IStack<Move> moveHistory = new Stack<>();
    private final PuzzleGenerator generator;

    /** Count of hints already consumed via {@link #requestHint()}. */
    private int hintsUsed;

    /**
     * Creates a game with a default {@link Random} source for puzzle generation.
     */
    public Game() {
        this(new Random());
    }

    /**
     * Creates a game with a supplied random source (useful for tests or reproducible puzzles).
     *
     * @param random source passed to {@link PuzzleGenerator}
     */
    public Game(Random random) {
        this.generator = new PuzzleGenerator(random);
    }

    /**
     * @return the board model (read and mutate only through game methods when enforcing rules)
     */
    public Board getBoard() {
        return board;
    }

    /**
     * @return undo stack of player moves (newest on top)
     */
    public IStack<Move> getMoveHistory() {
        return moveHistory;
    }

    /**
     * @return number of hints already applied in the current game
     */
    public int getHintsUsed() {
        return hintsUsed;
    }

    /**
     * @return remaining hint requests allowed ({@code MAX_HINTS - hintsUsed}, floored at 0)
     */
    public int getHintsRemaining() {
        return Math.max(0, MAX_HINTS - hintsUsed);
    }

    /**
     * @return {@code true} if the game has started, hints remain, and there are still empty cells
     */
    public boolean canRequestHint() {
        return !board.isStarted() || hintsUsed >= MAX_HINTS || board.getEmptyCells().isEmpty();
    }

    /**
     * Starts a fresh round: generates a puzzle, loads it on the board, clears history, and resets hint usage.
     */
    public void startNewGame() {
        PuzzleGenerator.GeneratedPuzzle generated = generator.generate(GIVENS_PER_BLOCK);
        board.loadPuzzle(generated.puzzle(), generated.solution());
        moveHistory.clear();
        hintsUsed = 0;
    }

    /**
     * Records a player edit and pushes it onto the undo stack.
     * <p>
     * No-op if the game has not started, the cell is a given clue, or the value is unchanged.
     * </p>
     *
     * @param row      target row
     * @param column   target column
     * @param newValue new digit or {@code null} to clear
     */
    public void applyPlayerMove(int row, int column, Integer newValue) {
        if (!board.isStarted()) {
            return;
        }

        Cell cell = board.getCell(row, column);
        if (cell.isGiven()) {
            return;
        }

        Integer previousValue = cell.getValue();
        if (previousValue == null && newValue == null) {
            return;
        }
        if (previousValue != null && previousValue.equals(newValue)) {
            return;
        }

        moveHistory.push(new Move(cell.getPosition(), previousValue, newValue));
        board.setPlayerValue(row, column, newValue);
    }

    /**
     * Reverts the most recent player move.
     *
     * @return the undone move, or empty if the stack was empty
     */
    public Optional<Move> undoLastMove() {
        if (moveHistory.isEmpty()) {
            return Optional.empty();
        }

        Move move = moveHistory.pop();
        Position position = move.getPosition();
        board.setPlayerValue(position.getRow(), position.getColumn(), move.getPreviousValue());
        return Optional.of(move);
    }

    /**
     * Picks a currently empty cell, writes the correct value on the board, and increments {@link #hintsUsed}.
     *
     * @return the applied hint, or empty if hints are not available
     */
    public Optional<Hint> requestHint() {
        if (canRequestHint()) {
            return Optional.empty();
        }

        IList<Cell> available = board.getEmptyCells();
        List<Cell> pool = new ArrayList<>();
        for (int i = 0; i < available.size(); i++) {
            Cell cell = available.get(i);
            if (cell.isEmpty()) {
                pool.add(cell);
            }
        }

        if (pool.isEmpty()) {
            return Optional.empty();
        }

        Collections.shuffle(pool);
        Cell cell = pool.get(0);
        Position position = cell.getPosition();
        int value = board.getSolutionValue(position.getRow(), position.getColumn());

        board.applyHint(position.getRow(), position.getColumn(), value);
        hintsUsed++;
        return Optional.of(new Hint(position, value));
    }
}
