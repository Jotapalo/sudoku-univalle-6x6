package com.sudoku.controller;

import com.sudoku.view.AnimatedSudokuBoardBackground;
import com.sudoku.view.GameStage;
import com.sudoku.view.InstructionsStage;
import com.sudoku.view.MenuStage;
import com.sudoku.model.StatisticsManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.OptionalLong;

/**
 * FXML controller for the main menu ({@code menu-view.fxml}).
 * <p>
 * Binds the animated background to the stack pane size and handles navigation to the game
 * or instructions windows. The game stage must <em>not</em> use {@code initOwner(menuStage)}:
 * hiding the menu would otherwise hide the owned child window and exit the app.
 * </p>
 */
public class MenuController implements Initializable {

    @FXML
    private javafx.scene.layout.StackPane menuStack;

    @FXML
    private AnimatedSudokuBoardBackground animatedBoard;

    @FXML
    private Label recordLabel;

    /**
     * Starts the background animation and binds its preferred size to {@link #menuStack}.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (animatedBoard != null && menuStack != null) {
            animatedBoard.prefWidthProperty().bind(menuStack.widthProperty());
            animatedBoard.prefHeightProperty().bind(menuStack.heightProperty());
            animatedBoard.minWidthProperty().bind(menuStack.widthProperty());
            animatedBoard.minHeightProperty().bind(menuStack.heightProperty());
            animatedBoard.startAnimation();
        } else if (animatedBoard != null) {
            animatedBoard.startAnimation();
        }

        updateRecordLabel();
    }

    /**
     * Shows the game window, starts a new round, then hides the menu.
     * Order matters: show game before hiding menu so at least one stage stays visible.
     */
    @FXML
    private void handleStartGame() {
        MenuStage menuStage = MenuStage.getInstance();
        GameStage gameStage = GameStage.getInstance();

        gameStage.show();
        gameStage.toFront();
        gameStage.getController().startGameFromMenu();
        menuStage.hide();
    }

    /**
     * Opens the instructions window in front of the menu.
     */
    @FXML
    private void handleInstructions() {
        InstructionsStage.getInstance().show();
        InstructionsStage.getInstance().toFront();
    }

    private void updateRecordLabel() {
        if (recordLabel == null) {
            return;
        }

        OptionalLong bestTime = StatisticsManager.readBestTimeMs();
        if (bestTime.isPresent()) {
            recordLabel.setText("Record: " + formatTime(bestTime.getAsLong()));
        } else {
            recordLabel.setText("Record: sin partidas guardadas");
        }
    }

    private String formatTime(long elapsedMs) {
        long totalSeconds = elapsedMs / 1000L;
        long minutes = totalSeconds / 60L;
        long seconds = totalSeconds % 60L;
        long centiseconds = (elapsedMs % 1000L) / 10L;
        return String.format("%02d:%02d.%02d", minutes, seconds, centiseconds);
    }
}
