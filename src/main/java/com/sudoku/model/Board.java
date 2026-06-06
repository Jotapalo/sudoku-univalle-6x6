package com.sudoku.model;

import com.sudoku.model.list.IList;
import com.sudoku.model.list.LinkedList;
import com.sudoku.model.tables.ITable;
import com.sudoku.model.tables.Table;

/**
 * 6×6 Sudoku board with validation, clue tracking, and a hidden full solution.
 * <p>
 * The grid uses 2×3 blocks (two rows, three columns per block). Cells are stored in an
 * {@link ITable} keyed by {@link Position}. Two {@link IList} instances maintain
 * {@link #emptyCells} (player-fillable empties) and {@link #errorCells} (cells in conflict).
 * </p>
 */
public class Board {

    /** Side length of the square grid (6). */
    public static final int SIZE = 6;
    /** Number of rows per 2×3 block (2). */
    public static final int BLOCK_ROWS = 2;
    /** Number of columns per 2×3 block (3). */
    public static final int BLOCK_COLS = 3;

    /** Maps each position to its {@link Cell} instance. */
    private final ITable<Position, Cell> cells = new Table<>();
    /** Cells that are empty and not given clues (updated on every value change). */
    private final IList<Cell> emptyCells = new LinkedList<>();
    /** Cells currently marked invalid after {@link #recomputeValidation()}. */
    private final IList<Cell> errorCells = new LinkedList<>();
    /** Complete solution for the active puzzle (used by hints and generator). */
    private final int[][] solution = new int[SIZE][SIZE];

    /** {@code true} after {@link #loadPuzzle(int[][], int[][])} has been called. */
    private boolean started;

    /**
     * Builds an empty 6×6 board: every cell exists, all are empty, and all are listed in {@link #emptyCells}.
     */
    public Board() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Position position = new Position(row, col);
                Cell cell = new Cell(position);
                cells.put(position, cell);
                emptyCells.add(cell);
            }
        }
    }

    /**
     * Returns the cell at row/column coordinates.
     *
     * @param row    zero-based row
     * @param column zero-based column
     * @return non-null cell at that position
     */
    public Cell getCell(int row, int column) {
        return cells.get(new Position(row, column));
    }

    /**
     * Returns the cell at the given position.
     *
     * @param position board coordinates
     * @return non-null cell, or {@code null} if the position were missing (should not happen)
     */
    public Cell getCell(Position position) {
        return cells.get(position);
    }

    /**
     * @return live list of cells that are empty and editable (not given clues)
     */
    public IList<Cell> getEmptyCells() {
        return emptyCells;
    }

    /**
     * @return live list of cells marked invalid by the last validation pass
     */
    public IList<Cell> getErrorCells() {
        return errorCells;
    }

    /**
     * @return {@code true} if a puzzle has been loaded and play is active
     */
    public boolean isStarted() {
        return started;
    }

    /**
     * Returns the correct digit from the hidden solution grid.
     *
     * @param row    zero-based row
     * @param column zero-based column
     * @return solution value 1–6
     */
    public int getSolutionValue(int row, int column) {
        return solution[row][column];
    }

    /**
     * Loads a new puzzle and its complete solution, resetting validation state.
     * <p>
     * Non-zero entries in {@code puzzle} become given clues; zeros become empty editable cells.
     * </p>
     *
     * @param puzzle       6×6 grid; 0 = empty, 1–6 = clue
     * @param fullSolution 6×6 complete valid solution matching the clues
     */
    public void loadPuzzle(int[][] puzzle, int[][] fullSolution) {
        clear();

        for (int row = 0; row < SIZE; row++) {
            System.arraycopy(fullSolution[row], 0, solution[row], 0, SIZE);
            for (int col = 0; col < SIZE; col++) {
                Cell cell = getCell(row, col);
                int value = puzzle[row][col];
                if (value != 0) {
                    cell.setValue(value);
                    cell.setGiven(true);
                    emptyCells.remove(cell);
                } else {
                    cell.setValue(null);
                    cell.setGiven(false);
                }
            }
        }

        started = true;
        recomputeValidation();
    }

    /**
     * Applies a player-entered value to an editable cell.
     * <p>
     * Ignores given clues. Clears the hinted flag and refreshes empty-cell tracking and validation.
     * </p>
     *
     * @param row    target row
     * @param column target column
     * @param value  digit 1–6, or {@code null} to clear
     */
    public void setPlayerValue(int row, int column, Integer value) {
        Cell cell = getCell(row, column);
        if (cell.isGiven()) {
            return;
        }

        cell.setValue(value);
        cell.setHinted(false);
        syncEmptyCell(cell);
        recomputeValidation();
    }

    /**
     * Fills an empty non-given cell with a hint value and marks it as hinted.
     *
     * @param row    target row
     * @param column target column
     * @param value  correct digit from {@link #solution}
     */
    public void applyHint(int row, int column, int value) {
        Cell cell = getCell(row, column);
        if (cell.isGiven() || !cell.isEmpty()) {
            return;
        }

        cell.setValue(value);
        cell.setHinted(true);
        syncEmptyCell(cell);
        recomputeValidation();
    }

    /**
     * Checks whether {@code value} appears elsewhere in the same row (excluding {@code ignoreColumn}).
     *
     * @param row          row to check
     * @param value        candidate digit
     * @param ignoreColumn column to skip (typically the cell being edited)
     * @return {@code false} if a duplicate exists in that row
     */
    public boolean isValidInRow(int row, int value, int ignoreColumn) {
        for (int col = 0; col < SIZE; col++) {
            if (col == ignoreColumn) {
                continue;
            }
            Cell cell = getCell(row, col);
            if (cell.getValue() != null && cell.getValue() == value) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether {@code value} appears elsewhere in the same column (excluding {@code ignoreRow}).
     */
    public boolean isValidInColumn(int column, int value, int ignoreRow) {
        for (int row = 0; row < SIZE; row++) {
            if (row == ignoreRow) {
                continue;
            }
            Cell cell = getCell(row, column);
            if (cell.getValue() != null && cell.getValue() == value) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether {@code value} appears elsewhere in the same 2×3 block as ({@code row}, {@code column}).
     */
    public boolean isValidInBlock(int row, int column, int value) {
        int startRow = (row / BLOCK_ROWS) * BLOCK_ROWS;
        int startCol = (column / BLOCK_COLS) * BLOCK_COLS;

        for (int r = startRow; r < startRow + BLOCK_ROWS; r++) {
            for (int c = startCol; c < startCol + BLOCK_COLS; c++) {
                if (r == row && c == column) {
                    continue;
                }
                Cell cell = getCell(r, c);
                if (cell.getValue() != null && cell.getValue() == value) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns whether placing {@code value} at ({@code row}, {@code column}) would satisfy
     * row, column, and block uniqueness (ignoring the cell itself).
     */
    public boolean isValidPlacement(int row, int column, int value) {
        return isValidInRow(row, value, column)
                && isValidInColumn(column, value, row)
                && isValidInBlock(row, column, value);
    }

    /**
     * Scans all rows, columns, and 2×3 blocks for duplicate filled values and updates
     * {@link Cell#setInvalid(boolean)} and {@link #errorCells}.
     *
     * @return {@code true} if at least one cell is invalid
     */
    public boolean recomputeValidation() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                getCell(row, col).setInvalid(false);
            }
        }
        errorCells.clear();

        for (int row = 0; row < SIZE; row++) {
            markDuplicatesInLine(row, true);
        }
        for (int col = 0; col < SIZE; col++) {
            markDuplicatesInLine(col, false);
        }
        for (int blockRow = 0; blockRow < SIZE / BLOCK_ROWS; blockRow++) {
            for (int blockCol = 0; blockCol < SIZE / BLOCK_COLS; blockCol++) {
                markDuplicatesInBlock(blockRow, blockCol);
            }
        }

        return !errorCells.isEmpty();
    }

    /**
     * Resets the board to the initial empty state (no active game).
     */
    public void clear() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                solution[row][col] = 0;
                Cell cell = getCell(row, col);
                cell.setValue(null);
                cell.setGiven(false);
                cell.setInvalid(false);
                cell.setHinted(false);
            }
        }

        emptyCells.clear();
        errorCells.clear();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                emptyCells.add(getCell(row, col));
            }
        }
        started = false;
    }

    /**
     * Keeps {@link #emptyCells} in sync when a cell becomes empty or filled.
     */
    private void syncEmptyCell(Cell cell) {
        if (cell.isEmpty() && !cell.isGiven()) {
            if (!emptyCells.contains(cell)) {
                emptyCells.add(cell);
            }
        } else {
            emptyCells.remove(cell);
        }
    }

    /**
     * Marks duplicate values along one row or one column.
     *
     * @param index zero-based row or column index
     * @param isRow {@code true} to scan a row, {@code false} to scan a column
     */
    private void markDuplicatesInLine(int index, boolean isRow) {
        for (int value = 1; value <= SIZE; value++) {
            IList<Cell> matches = new LinkedList<>();
            for (int i = 0; i < SIZE; i++) {
                Cell cell = isRow ? getCell(index, i) : getCell(i, index);
                if (cell.getValue() != null && cell.getValue() == value) {
                    matches.add(cell);
                }
            }
            if (matches.size() > 1) {
                for (int i = 0; i < matches.size(); i++) {
                    Cell cell = matches.get(i);
                    cell.setInvalid(true);
                    if (!errorCells.contains(cell)) {
                        errorCells.add(cell);
                    }
                }
            }
        }
    }

    /**
     * Marks duplicate values inside one 2×3 block.
     *
     * @param blockRow block row index (0..2)
     * @param blockCol block column index (0..1)
     */
    private void markDuplicatesInBlock(int blockRow, int blockCol) {
        int startRow = blockRow * BLOCK_ROWS;
        int startCol = blockCol * BLOCK_COLS;

        for (int value = 1; value <= SIZE; value++) {
            IList<Cell> matches = new LinkedList<>();
            for (int r = startRow; r < startRow + BLOCK_ROWS; r++) {
                for (int c = startCol; c < startCol + BLOCK_COLS; c++) {
                    Cell cell = getCell(r, c);
                    if (cell.getValue() != null && cell.getValue() == value) {
                        matches.add(cell);
                    }
                }
            }
            if (matches.size() > 1) {
                for (int i = 0; i < matches.size(); i++) {
                    Cell cell = matches.get(i);
                    cell.setInvalid(true);
                    if (!errorCells.contains(cell)) {
                        errorCells.add(cell);
                    }
                }
            }
        }
    }
}
