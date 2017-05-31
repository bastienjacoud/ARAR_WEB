package projet_web.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class User {

    public void receiveFile(InetAddress serveurIP, int serveurPort, String nomFichier) throws IOException {
        // Ouverture du port
        Socket soc = new Socket(serveurIP, serveurPort);

        // Ouverture du fichier
        FileOutputStream file = new FileOutputStream("src/projet_web/User/data/" + nomFichier);

        // Chargement des flux
        DataOutputStream clientDOS = new DataOutputStream(soc.getOutputStream());
        DataInputStream clientDIS = new DataInputStream(soc.getInputStream());

        // Ecriture de la requête GET
        clientDOS.writeUTF("GET " + nomFichier + " HTTP/1.1");
        clientDOS.flush();

        // Attente réponse, on récupère le header
        String header = clientDIS.readUTF();
        System.out.print(header);

        // On récupère la taille du fichier
        String contentLength = clientDIS.readUTF();
        System.out.print(contentLength);

        // On récupère le type du fichier
        String contentType = clientDIS.readUTF();
        System.out.print(contentType);

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
            System.out.print(b);
            file.write(b);
            b = clientDIS.read();
        }

        // Fermeture du fichier et des flux
        file.close();
        clientDIS.close();
        clientDOS.close();
    }

    public static void main(String[] args) throws IOException {
        //new User().receiveFile(InetAddress.getByName("127.0.0.1"), 80, "fichier.txt");
        new User().receiveFile(InetAddress.getByName("192.168.43.144"), 80, "moche.jpg");
    }
}
