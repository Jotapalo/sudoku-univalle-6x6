package com.sudoku;

import com.sudoku.view.MenuStage;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * JavaFX entry point for <strong>Sudoku36</strong>.
 * <p>
 * On launch, only the menu {@link com.sudoku.view.MenuStage} is shown. The game window is
 * opened later from {@link com.sudoku.controller.MenuController}. {@link Platform#setImplicitExit(boolean)}
 * is set to {@code false} so hiding the menu while switching to the game stage does not
 * terminate the JVM when no primary stage owns the remaining windows.
 * </p>
 */
public class Main extends Application {

    /**
     * Shows the singleton menu stage. The {@code primaryStage} from JavaFX is unused because
     * each screen uses its own {@link javafx.stage.Stage} subclass.
     *
     * @param primaryStage default stage provided by JavaFX (not used)
     */
    @Override
    public void start(Stage primaryStage) {
        Platform.setImplicitExit(true);
        MenuStage.getInstance().show();
    }

    /**
     * Standard launcher; delegates to {@link Application#launch(String...)}.
     *
     * @param args command-line arguments (ignored)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
