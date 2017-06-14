package projet_web.User;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.TreeSet;

import static java.lang.System.exit;

public class User extends Application {

    private Stage primaryStage;
    private AnchorPane gui;

    @FXML
    TextField serveurIP;
    @FXML
    TextField nomFichier;

    private Socket soc = null;
    private DataOutputStream clientDOS;

    private TreeSet<String> cookies;

    public void receiveFile(InetAddress serveurIP, int serveurPort, String nomFichier) {

        if (soc == null) {
            // Ouverture du port
            try {
                soc = new Socket(serveurIP, serveurPort);

                // Initialisation de la liste des cookies
                cookies = new TreeSet<>();
            } catch (Exception e) {
                showAlert("Erreur serveur", "Impossible de se connecter au serveur");
                return;
            }
        }

        // Ouverture du fichier
        FileOutputStream file = null;
        try {
            file = new FileOutputStream("src/projet_web/User/data/" + nomFichier);
        } catch (IOException e) {
            showAlert("Erreur locale", "Le répertoire 'src/projet_web/User/data' est introuvable");
            return;
        }

        try {
            // Chargement des flux
            clientDOS = new DataOutputStream(soc.getOutputStream());
            DataInputStream clientDIS = new DataInputStream(soc.getInputStream());

            // Ecriture de la requête GET
            clientDOS.writeUTF("GET " + nomFichier + " HTTP/1.1");
            clientDOS.flush();

            // Attente réponse, on récupère le header
            String header = clientDIS.readUTF();
            System.out.print(header);

            if (header.equals("HTTP/1.1 200 OK\n")) {

                // On récupère la taille du fichier
                String contentLength = clientDIS.readUTF();
                System.out.print(contentLength);

                // On récupère le type du fichier
                String contentType = clientDIS.readUTF();
                System.out.print(contentType);

                // On enregistre le cookie
                String cookie = clientDIS.readUTF();
                System.out.println(cookie);
                cookies.add(cookie);

                // On récupère le header des données
                String messageBody = clientDIS.readUTF();
                System.out.print(messageBody);

                // Ligne vide
                String emptyLine = clientDIS.readUTF();
                System.out.print(emptyLine);

                // Lecture des données
                int b = clientDIS.read();
                while (b != -1) {
                    // Ecriture dans le fichier
                    file.write(b);
                    b = clientDIS.read();
                }
                System.out.println("Le fichier a bien été reçu");
                showAlert("Succès", "Le fichier a bien été transféré");
            } else if (header.equals("HTTP/1.1 404 NOT FOUND\n")) {
                showAlert("Erreur serveur", "404 NOT FOUND : Le fichier est introuvable");
            } else if (header.equals("HTTP/1.1 502 BAD GATEWAY\n")) {
                showAlert("Erreur serveur", "502 BAD GATEWAY : La requête envoyée est incorrecte");
            } else {
                showAlert("Erreur serveur", "La réponse du serveur est incorrecte");
            }

            // Fermeture du fichier et des flux
            file.close();
            clientDIS.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Client HTTP");
        this.primaryStage.setResizable(false);

        // Permet l'arrêt du programme lorsque la fenêtre est quitée
        this.primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                if (soc != null) {
                    try {
                        clientDOS.writeUTF("Connection= close\n");
                        clientDOS.flush();
                        clientDOS.close();
                        soc.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Platform.exit();
                exit(0);
            }
        });

        // Chargement du rootLayout
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(User.class.getResource("User.fxml"));
        gui = (AnchorPane) loader.load();

        // Affichage du rootLayout
        Scene scene = new Scene(gui);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @FXML
    public void handleReceive() {
        Platform.runLater(
                () -> {
                    new Thread(() -> {
                        try {
                            new User().receiveFile(InetAddress.getByName(serveurIP.getText()), 80, nomFichier.getText());
                        } catch (UnknownHostException e) {
                            showAlert("Erreur locale", "L'adresse IP entrée pour le serveur est incorrecte");
                        }
                    }).start();
                }
        );
    }

    @FXML
    public void handleOpen() {
        try {
            Desktop.getDesktop().open(new File("src/projet_web/User/data"));
        } catch (IOException e) {
            showAlert("Erreur locale", "Le répertoire 'src/projet_web/User/data' est introuvable");
        }
    }

    private void showAlert(String titre, String message) {
        Platform.runLater(
                () -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(titre);
                    alert.setHeaderText(null);
                    alert.setContentText(message);
                    alert.showAndWait();
                }
        );
    }

    public static void main(String[] args) {
        launch(args);
    }
}
