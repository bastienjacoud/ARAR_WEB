package projet_web.ServeurWeb;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ServeurWeb {

    private ServerSocket ss;

    private static int i = 0;

    /*
     * Constructeur
     * Le serveur ecoute sur le port 80
     */
    public ServeurWeb() {
        try {
            ss = new ServerSocket(80);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*
     * Cree la connexion entre le client et le serveur
     */
    public Socket connexion() {
        Socket con_cli = null;
        if (ss != null)// Verifie qu'un socket a bien ete cree au prealable
        {
            con_cli = null;
            try {
                System.out.println("Attente de connexion avec un client...");
                con_cli = ss.accept();
                System.out.println("Un client vient de se connecter!");
                con_cli.setSoTimeout(10000);//met le temps d'attente a 10s
            } catch (IOException e) {

            }
        }
        return con_cli;
    }

    /*
     * Recupere le nom du fichier grace a la requete GET emise par le client
     */
    public String receiveRequest(Socket conn_cli, DataOutputStream serveurDOS, DataInputStream serveurDIS) throws IOException {
        try {
            //recuperation de la requete
            String req = "";
            req = serveurDIS.readUTF();
            System.out.println("Requête reçue.");
            if (req.split(" ")[0].compareTo("GET") == 0)//si c'est une methode GET
                return req.split(" ")[1];
            else if (req.compareTo("Connection= close\n") == 0)//si c'est une demande de fermeture de connexion
            {
                this.fermeConnexion(conn_cli);
                return "";
            } else//sinon (erreur)
            {
                serveurDOS.writeUTF("HTTP/1.1 502 BAD GATEWAY\n");
                return "";
            }
        } catch (SocketTimeoutException e)//si pas d'activite depuis 10s, on ferme la connexion
        {
            this.fermeConnexion(conn_cli);
            return "";
        }

    }

    /*
     * Envoi du contenu du fichier dont le nom est passe en parametre
     */
    public void sendFile(String nomFichier, Socket socket, DataOutputStream serveurDOS, DataInputStream serveurDIS) throws IOException {
        //Permet de ne pas envoyer de fichier lorsqu'il s'agit d'une fermeture de connexion ou d'une erreur
        if (nomFichier.compareTo("") != 0) {
            //Recuperation du fichier
            File f = new File("./src/projet_web/ServeurWeb/data/" + nomFichier);
            FileInputStream fis;
            try {
                //Creation du FileInputStream pour lire le fichier
                fis = new FileInputStream(f);

                // On récupère le cookie s'il y en a un
                String cookie;
                try {
                    socket.setSoTimeout(500);
                    cookie = serveurDIS.readUTF();
                } catch (SocketTimeoutException e) {
                    cookie = "Set-Cookie: idClient=" + i + "\n";
                    i++;
                }
                socket.setSoTimeout(10000);

                //Ecriture de l'entete
                serveurDOS.writeUTF("HTTP/1.1 200 OK\n");
                serveurDOS.writeUTF("Content-Length: " + f.length() + "\n");
                serveurDOS.writeUTF("Content-Type: " + nomFichier.split("\\.")[1] + "\n");
                serveurDOS.writeUTF(cookie);
                serveurDOS.writeUTF("Message_body: \n");
                serveurDOS.writeUTF("\n");
                //Lecture depuis le fichier et ecriture des donnees sur le flux
                int lect;
                while ((lect = fis.read()) >= 0) {
                    serveurDOS.write(lect);
                    serveurDOS.flush();
                }
                fis.close();

                System.out.println("Envoi terminé avec succès.");
            } catch (FileNotFoundException e) {
                //Si le fichier n'est pas trouvé, envoi message erreur au client
                serveurDOS.writeUTF("HTTP/1.1 404 NOT FOUND\n");
                serveurDOS.flush();
            }
        }

    }

    /*
     * Ferme la connexion etablie precedemment
     */
    public void fermeConnexion(Socket con_cli) {
        if (con_cli != null)//vérifie qu'un client est connecté
        {
            try {
                con_cli.close();
                System.out.println("Fermeture de la connexion.");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                System.out.println("Erreur lors de la fermeture des sockets !");
            }
        }
    }

    public void fermeConnexionStop() {
        try {
            ss.close();
            System.out.println("Fermeture de la connexion et du socket.");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("Erreur lors de la fermeture des sockets !");
        }
    }

    /*
     * Fonction effectuee par le serveur pour le lancer
     */
    public void action() {
        while (!ss.isClosed()) {
            Socket conn = this.connexion();
            if (conn != null) {
                new Thread(() -> {
                    try {
                        Socket con_cli = conn;

                        // On récupère les flux
                        DataOutputStream serveurDOS = new DataOutputStream(con_cli.getOutputStream());
                        DataInputStream serveurDIS = new DataInputStream(con_cli.getInputStream());
                        while (!ss.isClosed() && !con_cli.isClosed()) {
                            this.sendFile(this.receiveRequest(con_cli, serveurDOS, serveurDIS), con_cli, serveurDOS, serveurDIS);
                        }
                    } catch (IOException e) {

                    }
                }).start();
            }
        }
    }

}
