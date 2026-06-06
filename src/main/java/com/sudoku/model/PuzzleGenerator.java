package com.sudoku.model;

import com.sudoku.model.list.IList;
import com.sudoku.model.list.LinkedList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Builds playable 6×6 puzzles by solving an empty grid, then revealing clues per 2×3 block.
 * <p>
 * For each of the six blocks, exactly {@code givensPerBlock} random cells are copied from the
 * full solution into the puzzle grid; the rest stay empty (0).
 * </p>
 */
public class PuzzleGenerator {

    /**
     * Pair returned by {@link #generate(int)}: partial puzzle and its unique completion.
     *
     * @param puzzle   6×6 grid with clues (0 = empty)
     * @param solution 6×6 complete valid solution
     */
    public record GeneratedPuzzle(int[][] puzzle, int[][] solution) {}

    private final Random random;
    private final SudokuSolver solver;

    /**
     * @param random shared with {@link SudokuSolver} for varied solutions and clue placement
     */
    public PuzzleGenerator(Random random) {
        this.random = random;
        this.solver = new SudokuSolver(random);
    }

    /**
     * Generates a new puzzle with a fixed number of clues in every 2×3 block.
     *
     * @param givensPerBlock how many cells to reveal per block (must not exceed 6)
     * @return puzzle and solution matrices
     * @throws IllegalStateException if the solver cannot produce a full grid (should not occur for 6×6)
     */
    public GeneratedPuzzle generate(int givensPerBlock) {
        int[][] empty = new int[Board.SIZE][Board.SIZE];
        int[][] solution = solver.solve(empty);
        if (solution == null) {
            throw new IllegalStateException("Could not generate a valid solution");
        }

        int[][] puzzle = new int[Board.SIZE][Board.SIZE];
        IList<int[]> blockCoords = new LinkedList<>();

        for (int blockRow = 0; blockRow < Board.SIZE / Board.BLOCK_ROWS; blockRow++) {
            for (int blockCol = 0; blockCol < Board.SIZE / Board.BLOCK_COLS; blockCol++) {
                blockCoords.clear();

                int startRow = blockRow * Board.BLOCK_ROWS;
                int startCol = blockCol * Board.BLOCK_COLS;
                for (int r = startRow; r < startRow + Board.BLOCK_ROWS; r++) {
                    for (int c = startCol; c < startCol + Board.BLOCK_COLS; c++) {
                        blockCoords.add(new int[]{r, c});
                    }
                }

                List<int[]> coords = toMutableList(blockCoords);
                Collections.shuffle(coords, random);
                for (int i = 0; i < givensPerBlock; i++) {
                    int[] cell = coords.get(i);
                    puzzle[cell[0]][cell[1]] = solution[cell[0]][cell[1]];
                }
            }
        }

        return new GeneratedPuzzle(puzzle, solution);
    }

    /**
     * Copies an {@link IList} of int arrays into a mutable {@link List} for shuffling.
     */
    private List<int[]> toMutableList(IList<int[]> source) {
        List<int[]> list = new ArrayList<>(source.size());
        for (int i = 0; i < source.size(); i++) {
            list.add(source.get(i));
        }
        return list;
    }
}
