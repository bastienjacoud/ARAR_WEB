package projet_web.ServeurWeb;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;

    private Controller _controller;
    private ServeurWeb _serv;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Serveur HTTP");

        _serv = new ServeurWeb();
        _controller = new Controller();

        initRootLayout();

        showGUI();

    }

    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();

            loader.setLocation(Main.class.getResource("./Root.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the person overview inside the root layout.
     */
    public void showGUI() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("./VueServeur.fxml"));
            AnchorPane personOverview = (AnchorPane) loader.load();

            _controller = loader.getController();
            _controller.SetMain(this);

            rootLayout.setCenter(personOverview);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        this._serv.fermeConnexionStop();
    }

    public void LancerServ() {
        _serv.action();
    }

    /**
     * Returns the main stage.
     *
     * @return
     */
    public static void main(String[] args) {
        launch(args);
    }
}
