package com.sudoku.controller;

import com.sudoku.model.Board;
import com.sudoku.model.Cell;
import com.sudoku.model.Game;
import com.sudoku.model.Hint;
import com.sudoku.view.GameTimer;
import com.sudoku.view.MenuStage;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * FXML controller for the active game screen ({@code game-view.fxml}).
 * <p>
 * Responsibilities:
 * </p>
 * <ul>
 *   <li>Build and style the 6×6 {@link TextField} grid</li>
 *   <li>Forward keyboard input (digits 1–6, arrows, Tab, Delete) to {@link Game}</li>
 *   <li>Mirror {@link Board} state (given, invalid, hinted CSS classes)</li>
 *   <li>Run {@link GameTimer}, victory dialog, and navigation back to {@link MenuStage}</li>
 * </ul>
 */
public class GameController implements Initializable {

    private static final int SIZE = Board.SIZE;

    @FXML
    private GridPane boardGrid;

    @FXML
    private Label statusLabel;

    @FXML
    private Label timerLabel;

    @FXML
    private Button helpButton;

    /** Domain model for the current match. */
    private final Game game = new Game();
    /** UI text fields indexed by [row][column]. */
    private final TextField[][] cellFields = new TextField[SIZE][SIZE];
    /** Updates {@link #timerLabel} every second while a round is active. */
    private final GameTimer gameTimer = new GameTimer(time -> timerLabel.setText("⏱ " + time));

    /** Game window reference set by {@link com.sudoku.view.GameStage}. */
    private Stage stage;
    /** Currently highlighted cell coordinates, or -1 if none. */
    private int selectedRow = -1;
    private int selectedCol = -1;
    /** When {@code true}, text listeners ignore changes triggered by {@link #refreshBoardFromModel()}. */
    private boolean updatingView;
    /** Prevents showing the victory dialog more than once per completion. */
    private boolean victoryShown;

    /**
     * Builds the grid and shows a waiting message until {@link #startGameFromMenu()} runs.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        buildBoardGrid();
        refreshBoardFromModel();
        updateHelpButtonState();
        updateStatus("Esperando inicio de partida…", false, false);
    }

    /**
     * Stores the owning stage so {@link #returnToMenu()} can hide it.
     *
     * @param stage game window from {@link com.sudoku.view.GameStage}
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Called from the menu when the player starts a game: resets victory flag and deals a new puzzle.
     */
    public void startGameFromMenu() {
        victoryShown = false;
        startNewGameInternal();
    }

    /**
     * Stops the timer, hides the game stage, and shows the menu in front.
     */
    public void returnToMenu() {
        gameTimer.stop();
        if (stage != null) {
            stage.hide();
        }
        MenuStage.getInstance().show();
        MenuStage.getInstance().toFront();
    }

    /**
     * Confirms with the user, then starts a new puzzle and resets the timer.
     */
    @FXML
    private void handleNewGame() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Nuevo juego");
        confirm.setHeaderText("¿Iniciar un nuevo juego de Sudoku36?");
        confirm.setContentText("Se reiniciará el tablero y el cronómetro.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        victoryShown = false;
        startNewGameInternal();
    }

    /**
     * Returns to the menu after optional confirmation if a round is in progress.
     */
    @FXML
    private void handleBackToMenu() {
        if (game.getBoard().isStarted() && !victoryShown) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Volver al menú");
            confirm.setHeaderText("¿Salir de la partida actual?");
            confirm.setContentText("Tu progreso en este tablero se perderá.");

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.OK) {
                return;
            }
        }
        returnToMenu();
    }

    /**
     * Dequeues and applies one hint from {@link Game}, then refreshes the grid and status.
     */
    @FXML
    private void handleHelp() {
        if (!game.getBoard().isStarted()) {
            showInfo("Ayuda", "La partida aún no ha comenzado.");
            return;
        }

        if (game.getHintsRemaining() <= 0) {
            showInfo("Ayuda", "Has usado todas las ayudas disponibles.");
            updateHelpButtonState();
            return;
        }

        Optional<Hint> hint = game.requestHint();
        if (hint.isEmpty()) {
            showInfo("Ayuda", "No hay más sugerencias disponibles.");
            updateHelpButtonState();
            return;
        }

        refreshBoardFromModel();
        updateHelpButtonState();

        Hint applied = hint.get();
        focusCell(applied.getPosition().getRow(), applied.getPosition().getColumn());
        updateStatus("Sugerencia aplicada. Ayudas restantes: " + game.getHintsRemaining(), false, false);
    }

    /**
     * Handles arrow keys, Tab, Enter, Backspace, and Delete on a cell field.
     */
    @FXML
    private void handleCellKeyPressed(KeyEvent event) {
        TextField field = (TextField) event.getSource();
        int[] coords = findCoords(field);
        if (coords == null) {
            return;
        }

        int row = coords[0];
        int col = coords[1];

        switch (event.getCode()) {
            case UP -> {
                event.consume();
                moveFocus(row - 1, col);
            }
            case DOWN -> {
                event.consume();
                moveFocus(row + 1, col);
            }
            case LEFT -> {
                event.consume();
                moveFocus(row, col - 1);
            }
            case RIGHT, TAB -> {
                event.consume();
                if (event.isShiftDown()) {
                    moveFocus(row, col - 1);
                } else {
                    moveFocus(row, col + 1);
                }
            }
            case BACK_SPACE, DELETE -> {
                event.consume();
                clearCell(row, col, field);
            }
            case ENTER -> {
                event.consume();
                moveFocus(row + 1, col);
            }
            default -> {
            }
        }
    }

    /**
     * Generates a puzzle via {@link Game#startNewGame()}, starts the timer, and focuses the first editable cell.
     */
    private void startNewGameInternal() {
        game.startNewGame();
        victoryShown = false;
        gameTimer.start();
        refreshBoardFromModel();
        updateHelpButtonState();
        focusFirstEditableCell();
        updateStatus("¡A jugar! Completa el tablero.", false, true);
    }

    /**
     * Clears an editable cell and records the move on the model undo stack.
     */
    private void clearCell(int row, int col, TextField field) {
        if (!game.getBoard().isStarted() || !field.isEditable()) {
            return;
        }

        updatingView = true;
        field.clear();
        updatingView = false;

        game.applyPlayerMove(row, col, null);
        refreshBoardFromModel();
        updateValidationStatus();
    }

    /**
     * Moves keyboard focus to ({@code row}, {@code col}) if in bounds.
     */
    private void moveFocus(int row, int col) {
        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) {
            return;
        }
        focusCell(row, col);
    }

    /**
     * Requests focus and applies the {@code selected} style class.
     */
    private void focusCell(int row, int col) {
        cellFields[row][col].requestFocus();
        selectCell(row, col);
    }

    /**
     * Focuses the first non-given cell in row-major order.
     */
    private void focusFirstEditableCell() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (cellFields[row][col].isEditable()) {
                    focusCell(row, col);
                    return;
                }
            }
        }
    }

    /**
     * Moves to the next editable cell after ({@code fromRow}, {@code fromCol}) in row-major order.
     */
    private void advanceToNextEditable(int fromRow, int fromCol) {
        for (int r = fromRow; r < SIZE; r++) {
            int startCol = (r == fromRow) ? fromCol + 1 : 0;
            for (int c = startCol; c < SIZE; c++) {
                if (cellFields[r][c].isEditable()) {
                    focusCell(r, c);
                    return;
                }
            }
        }
    }

    /**
     * Creates 36 {@link TextField} cells with formatters, listeners, and block border styles.
     */
    private void buildBoardGrid() {
        boardGrid.getChildren().clear();

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                TextField field = new TextField();
                field.getStyleClass().add("sudoku-cell");
                applyBlockStyleClasses(field, row, col);
                field.setTextFormatter(createDigitFormatter());

                field.setOnMouseClicked(e -> selectCell(field));
                field.setOnKeyPressed(this::handleCellKeyPressed);

                int r = row;
                int c = col;
                field.focusedProperty().addListener((obs, oldVal, focused) -> {
                    if (focused) {
                        selectCell(r, c);
                    }
                });

                field.textProperty().addListener((obs, oldText, newText) -> {
                    if (updatingView) {
                        return;
                    }
                    onCellTextChanged(r, c, oldText, newText);
                });

                cellFields[row][col] = field;
                boardGrid.add(field, col, row);
            }
        }
    }

    /**
     * Syncs a typed digit or clear action to the model and revalidates.
     */
    private void onCellTextChanged(int row, int col, String oldText, String newText) {
        if (!game.getBoard().isStarted()) {
            updatingView = true;
            cellFields[row][col].clear();
            updatingView = false;
            updateStatus("La partida no está activa.", true, false);
            return;
        }

        if (newText.isBlank()) {
            if (!oldText.isBlank()) {
                game.applyPlayerMove(row, col, null);
                refreshBoardFromModel();
                updateValidationStatus();
            }
            return;
        }

        Integer newValue = Integer.parseInt(newText);
        game.applyPlayerMove(row, col, newValue);
        refreshBoardFromModel();
        updateValidationStatus();
        advanceToNextEditable(row, col);
    }

    /**
     * Updates the status line after validation, or triggers victory when the grid is complete and valid.
     */
    private void updateValidationStatus() {
        if (!game.getBoard().getErrorCells().isEmpty()) {
            updateStatus("Entrada inválida: evita duplicados en fila, columna o bloque.", true, false);
            return;
        }

        if (isPuzzleSolved()) {
            handleVictory();
            return;
        }

        updateStatus("Sin conflictos. ¡Continúa!", false, true);
    }

    /**
     * @return {@code true} when every cell is filled and none are marked invalid
     */
    private boolean isPuzzleSolved() {
        return game.getBoard().isStarted()
                && game.getBoard().getEmptyCells().isEmpty()
                && game.getBoard().getErrorCells().isEmpty();
    }

    /**
     * Stops the timer and shows the victory dialog once.
     */
    private void handleVictory() {
        if (victoryShown) {
            return;
        }
        victoryShown = true;
        gameTimer.stop();
        showVictoryDialog();
    }

    /**
     * Displays completion time and offers play again or return to menu.
     */
    private void showVictoryDialog() {
        String time = gameTimer.formatElapsedDetailed();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("¡Victoria!");
        alert.setHeaderText("¡Completaste Sudoku36 correctamente!");
        alert.setContentText("Tiempo empleado: " + time + "\n\n¿Qué deseas hacer ahora?");

        ButtonType playAgain = new ButtonType("Jugar de nuevo");
        ButtonType backToMenu = new ButtonType("Volver al menú");
        alert.getButtonTypes().setAll(playAgain, backToMenu);

        Optional<ButtonType> choice = alert.showAndWait();
        if (choice.isPresent() && choice.get() == playAgain) {
            victoryShown = false;
            startNewGameInternal();
        } else {
            returnToMenu();
        }
    }

    /**
     * Highlights one editable cell with the {@code selected} CSS class.
     */
    private void selectCell(int row, int col) {
        if (selectedRow == row && selectedCol == col) {
            return;
        }
        clearStyle("selected");
        selectedRow = row;
        selectedCol = col;
        if (!cellFields[row][col].getStyleClass().contains("given")) {
            cellFields[row][col].getStyleClass().add("selected");
        }
    }

    /**
     * Selects the cell that owns {@code field} and gives it focus.
     */
    private void selectCell(TextField field) {
        int[] coords = findCoords(field);
        if (coords != null) {
            selectCell(coords[0], coords[1]);
            field.requestFocus();
        }
    }

    /**
     * @return {@code int[]{row, col}} for {@code field}, or {@code null} if not found
     */
    private int[] findCoords(TextField field) {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (cellFields[r][c] == field) {
                    return new int[]{r, c};
                }
            }
        }
        return null;
    }

    /**
     * Removes {@code styleClass} from every cell field.
     */
    private void clearStyle(String styleClass) {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                cellFields[r][c].getStyleClass().remove(styleClass);
            }
        }
    }

    /**
     * Adds thick-border CSS classes for 2×3 block boundaries.
     */
    private void applyBlockStyleClasses(TextField field, int row, int col) {
        if (row % Board.BLOCK_ROWS == 0) {
            field.getStyleClass().add("block-top");
        }
        if (col % Board.BLOCK_COLS == 0) {
            field.getStyleClass().add("block-left");
        }
        if ((row + 1) % Board.BLOCK_ROWS == 0) {
            field.getStyleClass().add("block-bottom");
        }
        if ((col + 1) % Board.BLOCK_COLS == 0) {
            field.getStyleClass().add("block-right");
        }
    }

    /**
     * Copies all cell values and style flags from {@link Board} into the text fields.
     */
    private void refreshBoardFromModel() {
        updatingView = true;
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Cell cell = game.getBoard().getCell(row, col);
                TextField field = cellFields[row][col];

                Integer value = cell.getValue();
                field.setText(value == null ? "" : String.valueOf(value));
                field.setEditable(!cell.isGiven());

                applyCellVisualState(field, cell);
            }
        }
        updatingView = false;

        if (selectedRow >= 0 && selectedCol >= 0) {
            selectCell(selectedRow, selectedCol);
        }
    }

    /**
     * Sets CSS classes {@code given}, {@code invalid}, or {@code hinted} on one field.
     */
    private void applyCellVisualState(TextField field, Cell cell) {
        field.getStyleClass().removeAll("given", "invalid", "hinted", "selected");

        if (cell.isGiven()) {
            field.getStyleClass().add("given");
        }
        if (cell.isInvalid()) {
            field.getStyleClass().add("invalid");
        } else if (cell.isHinted()) {
            field.getStyleClass().add("hinted");
        }
    }

    /**
     * Restricts input to a single digit 1–6 or empty string.
     */
    private TextFormatter<String> createDigitFormatter() {
        return new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty()) {
                return change;
            }
            return newText.matches("[1-6]") ? change : null;
        });
    }

    /**
     * Disables the help button when no hints can be requested.
     */
    private void updateHelpButtonState() {
        helpButton.setDisable(!game.getBoard().isStarted() || game.canRequestHint());
    }

    /**
     * Sets status text and optional {@code error} / {@code success} style classes.
     */
    private void updateStatus(String message, boolean error, boolean success) {
        statusLabel.setText(message);
        statusLabel.getStyleClass().removeAll("error", "success");
        if (error) {
            statusLabel.getStyleClass().add("error");
        } else if (success) {
            statusLabel.getStyleClass().add("success");
        }
    }

    /**
     * Shows a simple information alert.
     */
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
