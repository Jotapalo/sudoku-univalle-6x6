package com.sudoku.view;

import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;

/**
 * Loads the application window icon from the classpath and attaches it to JavaFX stages.
 * <p>
 * Place the PNG at {@value #ICON_PATH} under {@code src/main/resources}.
 * If the resource is missing, stages still work but show the default JavaFX icon.
 * </p>
 */
public final class AppIcons {

    /**
     * Classpath location of the window icon (e.g. {@code src/main/resources/com/sudoku/icon.png}).
     */
    public static final String ICON_PATH = "/com/sudoku/icon.png";

    private AppIcons() {
    }

    /**
     * Adds the Sudoku36 icon to {@code stage} when the resource file exists.
     *
     * @param stage window that should display the icon in the title bar and task switcher
     */
    public static void applyTo(Stage stage) {
        URL iconUrl = AppIcons.class.getResource(ICON_PATH);
        if (iconUrl != null) {
            stage.getIcons().add(new Image(Objects.requireNonNull(iconUrl).toExternalForm()));
        }
    }
}