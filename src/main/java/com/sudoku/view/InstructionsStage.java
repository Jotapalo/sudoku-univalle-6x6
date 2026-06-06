package com.sudoku.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Singleton instructions dialog (non-modal) with gameplay help text.
 * <p>
 * Loaded from {@code /com/sudoku/instructions-view.fxml}. Can be shown alongside the menu
 * without blocking it ({@link Modality#NONE}).
 * </p>
 */
public class InstructionsStage extends Stage {

    private static InstructionsStage instance;

    /**
     * Private constructor: loads FXML and shared stylesheet.
     *
     * @throws IllegalStateException if {@code instructions-view.fxml} cannot be loaded
     */
    private InstructionsStage() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(getClass().getResource("/com/sudoku/instructions-view.fxml"))
            );
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(
                    Objects.requireNonNull(getClass().getResource("/com/sudoku/styles.css")).toExternalForm()
            );

            setTitle("Instrucciones — Sudoku36");
            setScene(scene);
            AppIcons.applyTo(this);
            initModality(Modality.NONE);
            setResizable(true);
            setMinWidth(480);
            setMinHeight(520);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load instructions-view.fxml", e);
        }
    }

    /**
     * @return the single instructions stage, creating it on first access
     */
    public static InstructionsStage getInstance() {
        if (instance == null) {
            instance = new InstructionsStage();
        }
        return instance;
    }
}
