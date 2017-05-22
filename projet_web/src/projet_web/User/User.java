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
        FileOutputStream file = new FileOutputStream("data/" + nomFichier);

        // Chargement des flux
        DataOutputStream clientDOS = new DataOutputStream(soc.getOutputStream());
        DataInputStream clientDIS = new DataInputStream(soc.getInputStream());

        // Ecriture de la requête GET
        clientDOS.write(("GET " + nomFichier + " HTTP/1.1\n\r").getBytes());
        clientDOS.flush();

        int nb = 0;
        do {
            // Attente réponse
            String header = clientDIS.readUTF();
            nb += header.length();
            boolean data = false;

            if (header.equals("HTTP/1.1 200 OK")) {
                byte[] buf = new byte[2056];
                while (clientDIS.read(buf) > 0) {
                    nb++;
                    if (new String(buf).trim().equals("Message_body:") && !data) {
                        buf = new byte[2056];
                        data = true;
                    }
                }
                file.write(buf);
            }

        } while (nb == 2056);
    }

    public static void main(String[] args) throws IOException {
        new User().receiveFile(InetAddress.getByName("192.168.43.144"),80,"");
    }
}
