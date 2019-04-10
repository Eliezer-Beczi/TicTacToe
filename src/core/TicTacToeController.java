package core;

import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class TicTacToeController implements Initializable {
    private int size;
    public GridPane gridPane;
    private ArrayList<Label> labels;

    private int depth;
    private AIPlayer aiPlayer;
    private int requiredSymbols;

    private String playerSymbol;
    private String playerColor;

    private String computerSymbol;
    private String computerColor;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        labels = new ArrayList<>();
        gridPane.setGridLinesVisible(true);
    }

    public void load(int size, boolean playerStarts) {
        if (playerStarts) {
            playerSymbol = "X";
            playerColor = "red";

            computerSymbol = "O";
            computerColor = "blue";
        } else {
            playerSymbol = "O";
            playerColor = "blue";

            computerSymbol = "X";
            computerColor = "red";
        }

        switch (size) {
            case 3:
                depth = -1;
                requiredSymbols = 3;
                break;
            case 4:
                depth = 8;
                requiredSymbols = 4;
                break;
            default:
                depth = 3;
                requiredSymbols = 5;
        }

        this.size = size;
        aiPlayer = new AIPlayer(size, requiredSymbols, computerSymbol, playerSymbol);

        Platform.runLater(() -> {
            setup();

            if (!playerStarts) {
                int[] move = aiPlayer.next(depth);
                int index = move[0] * size + move[1];

                labels.get(index).setTextFill(Paint.valueOf(computerColor));
                labels.get(index).setText(computerSymbol);
                aiPlayer.updateBoard(move[0], move[1], computerSymbol);
            }
        });
    }

    private void setup() {
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                Label label = new Label("");
                label.setMinSize(32, 32);
                label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                label.setAlignment(Pos.CENTER);
                label.setFont(Font.font("Verdana", FontWeight.NORMAL, 20));

                final int row = i;
                final int col = j;

                label.setOnMouseClicked(mouseEvent -> {
                    if (!label.getText().isEmpty()) {
                        return;
                    }

                    label.setTextFill(Paint.valueOf(playerColor));
                    label.setText(playerSymbol);
                    aiPlayer.updateBoard(row, col, playerSymbol);

                    if (gameOver()) {
                        return;
                    }

                    int[] move = aiPlayer.next(depth);
                    int index = move[0] * size + move[1];

                    labels.get(index).setTextFill(Paint.valueOf(computerColor));
                    labels.get(index).setText(computerSymbol);
                    aiPlayer.updateBoard(move[0], move[1], computerSymbol);

                    if (gameOver()) {
                        return;
                    }
                });

                gridPane.add(label, j, i);
                GridPane.setFillWidth(label, true);
                GridPane.setFillHeight(label, true);

                labels.add(label);
            }
        }
    }

    private boolean gameOver() {
        int result = aiPlayer.gameOver();

        if (result == 42) {
            return false;
        }

        if (result == 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Keep calm...");
            alert.setContentText("It's a Draw!");
            alert.showAndWait();
            closeWindow();
        } else {
            if ((result == 1 && playerSymbol.equals("X")) || (result == -1 && playerSymbol.equals("O"))) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Congratulations!");
                alert.setContentText("You Win!");
                alert.showAndWait();
                closeWindow();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("The game you just can't win.");
                alert.setContentText("You Lose!");
                alert.showAndWait();
                closeWindow();
            }
        }

        return true;
    }

    private void closeWindow() {
        Stage stage = (Stage) gridPane.getScene().getWindow();
        stage.close();
    }
}
