package com.sudoku.controller;

import com.sudoku.view.InstructionsStage;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML controller for the instructions dialog ({@code instructions-view.fxml}).
 * <p>
 * Builds styled {@link Text} nodes programmatically because CSS does not reliably style
 * individual runs inside a {@link TextFlow}. Headings use gold ({@link #HEADING_COLOR});
 * body text uses cream ({@link #BODY_COLOR}).
 * </p>
 */
public class InstructionsController implements Initializable {

    /** Body text color (warm cream). */
    private static final Color BODY_COLOR = Color.web("#FFF1E6");
    /** Short uppercase section titles. */
    private static final Color HEADING_COLOR = Color.web("#FDE68A");

    @FXML
    private TextFlow instructionsText;

    /**
     * Populates the text flow with localized Spanish instructions and styling.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instructionsText.getStyleClass().add("instructions-body");
        instructionsText.getChildren().setAll(buildStyledInstructions());
    }

    /**
     * Hides the singleton instructions stage.
     */
    @FXML
    private void handleClose() {
        InstructionsStage.getInstance().hide();
    }

    /**
     * Splits content on blank lines and applies heading vs body fonts/colors.
     *
     * @return one {@link Text} node per paragraph block
     */
    private Text[] buildStyledInstructions() {
        String content = buildInstructionsText();
        String[] blocks = content.split("\n\n");
        Text[] nodes = new Text[blocks.length];

        for (int i = 0; i < blocks.length; i++) {
            String block = blocks[i].trim();
            boolean isHeading = block.equals(block.toUpperCase()) && block.length() < 40;

            Text text = new Text(block + (i < blocks.length - 1 ? "\n\n" : ""));
            text.setFill(isHeading ? HEADING_COLOR : BODY_COLOR);
            text.setFont(isHeading
                    ? Font.font("Segoe UI", FontWeight.BOLD, 15)
                    : Font.font("Segoe UI", 14));
            nodes[i] = text;
        }
        return nodes;
    }

    /**
     * @return full instructions copy shown in the dialog (Spanish, Sudoku36 rules)
     */
    private String buildInstructionsText() {
        return """
                SUDOKU36 — OBJETIVO
                Completa la cuadrícula 6×6 con los números del 1 al 6, de modo que cada fila, \
                cada columna y cada bloque 2×3 contenga todos los números sin repetir.

                INICIO
                • En el menú principal pulsa «Iniciar juego».
                • Se generará un tablero con pistas iniciales (2 números por bloque 2×3).
                • Las celdas grises con número son fijas y no se pueden modificar.

                CONTROLES DE TECLADO
                • Flechas ↑ ↓ ← → : moverte entre celdas.
                • Tab / Shift+Tab : celda siguiente / anterior.
                • Teclas 1 a 6 : escribir un número.
                • Supr o Retroceso : borrar la celda seleccionada.
                • Enter : bajar a la fila siguiente.

                VALIDACIÓN
                • El juego valida en tiempo real filas, columnas y bloques.
                • Si hay un conflicto, las celdas se marcan en rojo y verás un mensaje de error.

                AYUDA
                • El botón «Ayuda» sugiere un número válido para una celda vacía.
                • Hay un máximo de 10 ayudas por partida; no revela la solución completa.

                NUEVO JUEGO
                • Durante la partida puedes pulsar «Nuevo juego» para reiniciar (con confirmación).
                • «Volver al menú» te regresa a la pantalla de inicio.

                CRONÓMETRO Y VICTORIA
                • Al iniciar la partida comienza el cronómetro.
                • Al completar el sudoku correctamente verás tu tiempo y podrás jugar de nuevo \
                o volver al menú.
                """;
    }
}
