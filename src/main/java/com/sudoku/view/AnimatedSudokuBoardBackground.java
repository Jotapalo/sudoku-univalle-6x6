package com.sudoku.view;

import com.sudoku.model.Board;
import com.sudoku.model.PuzzleGenerator;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.Random;

/**
 * Decorative menu background: a centered, scaled 6×6 grid whose digits fade in and out.
 * <p>
 * Mouse-transparent so it does not block menu buttons. Every {@value #BOARD_CYCLE} seconds a
 * new random full solution is loaded; each cell runs an independent appear/hold/disappear loop
 * with randomized delays and opacities.
 * </p>
 */
public class AnimatedSudokuBoardBackground extends StackPane {

    /** Base pixel size of one cell before scaling. */
    private static final int BASE_CELL_SIZE = 52;
    /** Interval between loading a new random solved board. */
    private static final Duration BOARD_CYCLE = Duration.seconds(16);
    /** Duration of each fade-in or fade-out transition. */
    private static final Duration FADE_DURATION = Duration.millis(900);

    private final Random random = new Random();
    private final PuzzleGenerator generator = new PuzzleGenerator(random);
    private final GridPane grid = new GridPane();
    private final Label[][] digitLabels = new Label[Board.SIZE][Board.SIZE];
    private final StackPane[][] cellPanes = new StackPane[Board.SIZE][Board.SIZE];

    private Timeline boardCycleTimeline;

    /**
     * Builds the grid, marks the pane non-interactive, and listens for resize to rescale.
     */
    public AnimatedSudokuBoardBackground() {
        setMouseTransparent(true);
        getStyleClass().add("animated-board-bg");
        setAlignment(Pos.CENTER);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        setMinSize(0, 0);

        buildGrid();
        getChildren().add(grid);

        widthProperty().addListener((obs, oldW, newW) -> updateBoardScale());
        heightProperty().addListener((obs, oldH, newH) -> updateBoardScale());
    }

    /**
     * Loads the first board, starts periodic board refresh, and applies initial scale.
     */
    public void startAnimation() {
        loadRandomBoard();
        if (boardCycleTimeline != null) {
            boardCycleTimeline.stop();
        }
        boardCycleTimeline = new Timeline(new KeyFrame(BOARD_CYCLE, e -> loadRandomBoard()));
        boardCycleTimeline.setCycleCount(Animation.INDEFINITE);
        boardCycleTimeline.play();
        updateBoardScale();
    }

    /**
     * Stops the board-cycle timeline (e.g. when leaving the menu).
     */
    public void stopAnimation() {
        if (boardCycleTimeline != null) {
            boardCycleTimeline.stop();
        }
    }

    /**
     * Creates 36 labels in a {@link GridPane} with block border style classes.
     */
    private void buildGrid() {
        grid.getStyleClass().add("animated-board-grid");
        grid.setHgap(0);
        grid.setVgap(0);
        grid.setAlignment(Pos.CENTER);

        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                Label label = new Label();
                label.getStyleClass().add("animated-board-cell");
                label.setOpacity(0);
                label.setMouseTransparent(true);

                digitLabels[row][col] = label;
                StackPane cellPane = new StackPane(label);
                cellPane.getStyleClass().add("animated-board-slot");
                cellPane.setAlignment(Pos.CENTER);
                cellPane.setMinSize(BASE_CELL_SIZE, BASE_CELL_SIZE);
                cellPane.setPrefSize(BASE_CELL_SIZE, BASE_CELL_SIZE);
                cellPane.setMaxSize(BASE_CELL_SIZE, BASE_CELL_SIZE);
                applyBlockStyle(cellPane, row, col);

                cellPanes[row][col] = cellPane;
                grid.add(cellPane, col, row);
            }
        }
    }

    /**
     * Scales the grid to roughly 88% of the smaller parent dimension, clamped between 1.0 and 1.75.
     */
    private void updateBoardScale() {
        double w = getWidth();
        double h = getHeight();
        if (w <= 0 || h <= 0) {
            return;
        }

        double baseBoard = Board.SIZE * BASE_CELL_SIZE;
        double target = Math.min(w, h) * 0.88;
        double scale = target / baseBoard;
        scale = Math.max(1.0, Math.min(scale, 1.75));

        grid.setScaleX(scale);
        grid.setScaleY(scale);
    }

    /**
     * Generates a puzzle with two givens per block but displays the full solution digits for effect.
     */
    private void loadRandomBoard() {
        PuzzleGenerator.GeneratedPuzzle puzzle = generator.generate(2);
        int[][] solution = puzzle.solution();

        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                Label label = digitLabels[row][col];
                label.setOpacity(0);
                label.setText(String.valueOf(solution[row][col]));
                scheduleAppearDisappear(label);
            }
        }
    }

    /**
     * Waits a random delay before starting the fade loop for one label.
     */
    private void scheduleAppearDisappear(Label label) {
        PauseTransition initialDelay = new PauseTransition(Duration.millis(random.nextInt(1200)));
        initialDelay.setOnFinished(e -> playAppearDisappearLoop(label));
        initialDelay.play();
    }

    /**
     * Repeating fade-in, hold, fade-out sequence with randomized timing and peak opacity.
     */
    private void playAppearDisappearLoop(Label label) {
        double visibleOpacity = 0.45 + random.nextDouble() * 0.5;

        FadeTransition fadeIn = new FadeTransition(FADE_DURATION, label);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(visibleOpacity);

        PauseTransition hold = new PauseTransition(Duration.millis(350 + random.nextInt(650)));

        FadeTransition fadeOut = new FadeTransition(FADE_DURATION, label);
        fadeOut.setFromValue(visibleOpacity);
        fadeOut.setToValue(0);

        PauseTransition gap = new PauseTransition(Duration.millis(250 + random.nextInt(750)));

        SequentialTransition cycle = new SequentialTransition(fadeIn, hold, fadeOut, gap);
        cycle.setOnFinished(e -> playAppearDisappearLoop(label));
        cycle.play();
    }

    /**
     * Adds CSS classes for thick borders between 2×3 blocks (same logic as the game grid).
     */
    private void applyBlockStyle(javafx.scene.Node node, int row, int col) {
        if (row % Board.BLOCK_ROWS == 0) {
            node.getStyleClass().add("block-top");
        }
        if (col % Board.BLOCK_COLS == 0) {
            node.getStyleClass().add("block-left");
        }
        if ((row + 1) % Board.BLOCK_ROWS == 0) {
            node.getStyleClass().add("block-bottom");
        }
        if ((col + 1) % Board.BLOCK_COLS == 0) {
            node.getStyleClass().add("block-right");
        }
    }
}
