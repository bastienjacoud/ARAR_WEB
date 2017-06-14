package projet_web.ServeurWeb;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Controller {

    @FXML
    private Button btLancement;

    private Main _main;

    public Controller() {

    }

    @FXML
    private void Initialize() {

    }

    private void LancerServ() {
        btLancement.setDisable(true);
        Platform.runLater(() -> {
            final Service<Void> LancerServ = new Service<Void>() {

                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {

                        @Override
                        protected Void call() throws Exception {
                            _main.LancerServ();
                            return null;
                        }
                    };
                }
            };
            LancerServ.start();
        });
    }

    public void SetMain(Main main) {
        this._main = main;
        btLancement.setOnMouseClicked(mouseEvent -> LancerServ());
    }

    @FXML
    public void handleOpen() {
        try {
            Desktop.getDesktop().open(new File("src/projet_web/ServeurWeb/data"));
        } catch (IOException e) {
            Platform.runLater(
                    () -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Erreur locale");
                        alert.setHeaderText(null);
                        alert.setContentText("Le répertoire 'src/projet_web/ServeurWeb/data' est introuvable");
                        alert.showAndWait();
                    }
            );
        }
    }

}
