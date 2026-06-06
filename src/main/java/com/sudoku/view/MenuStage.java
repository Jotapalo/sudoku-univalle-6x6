package com.sudoku.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Singleton main-menu window: title screen, animated background, and navigation buttons.
 * <p>
 * Loaded from {@code /com/sudoku/menu-view.fxml}. Only one instance exists for the
 * application lifetime ({@link #getInstance()}).
 * </p>
 */
public class MenuStage extends Stage {

    private static MenuStage instance;

    /**
     * Private constructor: loads FXML, applies {@link AppIcons} and stylesheet.
     *
     * @throws IllegalStateException if {@code menu-view.fxml} cannot be loaded
     */
    private MenuStage() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(getClass().getResource("/com/sudoku/menu-view.fxml"))
            );
            Scene scene = new Scene(loader.load(), 720, 680);
            scene.getStylesheets().add(
                    Objects.requireNonNull(getClass().getResource("/com/sudoku/styles.css")).toExternalForm()
            );

            setTitle("Sudoku36 — Menú");
            setScene(scene);
            AppIcons.applyTo(this);
            setMinWidth(640);
            setMinHeight(600);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load menu-view.fxml", e);
        }
    }

    /**
     * @return the single menu stage, creating it on first access
     */
    public static MenuStage getInstance() {
        if (instance == null) {
            instance = new MenuStage();
        }
        return instance;
    }
}
