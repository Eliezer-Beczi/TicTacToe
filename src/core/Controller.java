package core;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public TextField sizeField;
    public Button startButton;
    public CheckBox firstMoveCheckBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void startTicTacToe(ActionEvent actionEvent) {
        if (sizeField.getText().isEmpty()) {
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("ticTacToe.fxml"));

        try {
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setMinWidth(500);
            stage.setMinHeight(500);

            TicTacToeController controller = loader.getController();
            controller.load(Integer.parseInt(sizeField.getText()), firstMoveCheckBox.isSelected());

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
