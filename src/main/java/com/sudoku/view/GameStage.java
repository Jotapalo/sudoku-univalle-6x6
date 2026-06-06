package com.sudoku.view;

import com.sudoku.controller.GameController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Singleton game window containing the 6×6 grid, timer, and action buttons.
 * <p>
 * Loaded from {@code /com/sudoku/game-view.fxml}. Closing the window (X) returns to the
 * menu instead of exiting the application. The same stage is reused for every new game.
 * </p>
 */
public class GameStage extends Stage {

    private static GameStage instance;

    private final GameController controller;

    /**
     * Loads FXML, wires the {@link GameController}, and registers close handling.
     *
     * @throws IllegalStateException if {@code game-view.fxml} cannot be loaded
     */
    private GameStage() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(getClass().getResource("/com/sudoku/game-view.fxml"))
            );
            Scene scene = new Scene(loader.load(), 820, 720);
            scene.getStylesheets().add(
                    Objects.requireNonNull(getClass().getResource("/com/sudoku/styles.css")).toExternalForm()
            );

            controller = loader.getController();
            controller.setStage(this);

            setTitle("Sudoku36 — Partida");
            setScene(scene);
            AppIcons.applyTo(this);
            setMinWidth(760);
            setMinHeight(680);

            setOnCloseRequest(event -> {
                event.consume();
                controller.returnToMenu();
            });
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load game-view.fxml", e);
        }
    }

    /**
     * @return the single game stage, creating it on first access
     */
    public static GameStage getInstance() {
        if (instance == null) {
            instance = new GameStage();
        }
        return instance;
    }

    /**
     * @return FXML controller for this window (puzzle logic and UI binding)
     */
    public GameController getController() {
        return controller;
    }
}
