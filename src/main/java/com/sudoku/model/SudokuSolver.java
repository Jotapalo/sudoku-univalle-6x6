package com.sudoku.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Backtracking solver for 6×6 Sudoku with minimum-remaining-values (MRV) cell ordering.
 * <p>
 * Used by {@link PuzzleGenerator} to build a full solution before clues are carved out.
 * Candidate digits are shuffled with the supplied {@link Random} so generated puzzles vary.
 * </p>
 */
public class SudokuSolver {

    private final Random random;

    /**
     * @param random used to shuffle candidate digit order during search
     */
    public SudokuSolver(Random random) {
        this.random = random;
    }

    /**
     * Attempts to complete the grid in place on a copy of {@code grid}.
     *
     * @param grid 6×6 array; {@code 0} or unused cells represent empties
     * @return a new grid with a full valid solution, or {@code null} if unsatisfiable
     */
    public int[][] solve(int[][] grid) {
        int[][] copy = copy(grid);
        return solveRecursive(copy) ? copy : null;
    }

    /**
     * Recursive DFS: picks the empty cell with fewest candidates (MRV), tries shuffled digits.
     */
    private boolean solveRecursive(int[][] grid) {
        int[] cell = findEmptyWithMrv(grid);
        if (cell == null) {
            return true;
        }

        int row = cell[0];
        int col = cell[1];
        List<Integer> candidates = candidates(grid, row, col);
        Collections.shuffle(candidates, random);

        for (int value : candidates) {
            grid[row][col] = value;
            if (solveRecursive(grid)) {
                return true;
            }
            grid[row][col] = 0;
        }
        return false;
    }

    /**
     * Finds the empty cell with the smallest candidate set (ties broken by scan order).
     *
     * @return {@code int[]{row, col}} or {@code null} if the grid is full
     */
    private int[] findEmptyWithMrv(int[][] grid) {
        int bestCount = Integer.MAX_VALUE;
        int[] best = null;

        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                if (grid[row][col] != 0) {
                    continue;
                }
                int count = candidates(grid, row, col).size();
                if (count == 0) {
                    return new int[]{row, col};
                }
                if (count < bestCount) {
                    bestCount = count;
                    best = new int[]{row, col};
                }
            }
        }
        return best;
    }

    /**
     * Lists digits 1–6 that can legally be placed at ({@code row}, {@code col}) in {@code grid}.
     */
    private List<Integer> candidates(int[][] grid, int row, int col) {
        List<Integer> result = new ArrayList<>(Board.SIZE);
        for (int value = 1; value <= Board.SIZE; value++) {
            if (canPlace(grid, row, col, value)) {
                result.add(value);
            }
        }
        return result;
    }

    /**
     * Returns whether {@code value} is absent from the row, column, and 2×3 block of ({@code row}, {@code col}).
     */
    private boolean canPlace(int[][] grid, int row, int col, int value) {
        for (int c = 0; c < Board.SIZE; c++) {
            if (grid[row][c] == value) {
                return false;
            }
        }
        for (int r = 0; r < Board.SIZE; r++) {
            if (grid[r][col] == value) {
                return false;
            }
        }

        int startRow = (row / Board.BLOCK_ROWS) * Board.BLOCK_ROWS;
        int startCol = (col / Board.BLOCK_COLS) * Board.BLOCK_COLS;
        for (int r = startRow; r < startRow + Board.BLOCK_ROWS; r++) {
            for (int c = startCol; c < startCol + Board.BLOCK_COLS; c++) {
                if (grid[r][c] == value) {
                    return false;
                }
            }
        }
        return true;
    }

    /** Deep-copies a 6×6 grid. */
    private int[][] copy(int[][] grid) {
        int[][] out = new int[Board.SIZE][Board.SIZE];
        for (int r = 0; r < Board.SIZE; r++) {
            System.arraycopy(grid[r], 0, out[r], 0, Board.SIZE);
        }
        return out;
    }
}
