package projet_web.ServeurWeb;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServeurWeb {

	private ServerSocket ss;
	private Socket con_cli;
	private DataOutputStream serveurDOS;
	private DataInputStream serveurDIS;

	/*
	 * Constructeur
	 * Le serveur ecoute sur le port 80
	 */
	public ServeurWeb()
	{
		try
		{
			ss = new ServerSocket(80);
			con_cli = null;
			serveurDOS = null;
			serveurDIS = null;
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * Cree la connexion entre le client et le serveur
	 */
	public void connexion()
	{
		if(ss != null)
		{
			try
			{
				//con_cli = new Socket();
				System.out.println("Attente de connexion avec un client...");
				con_cli = ss.accept();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			if(con_cli != null)
			{
				try
				{
					serveurDOS = new DataOutputStream(con_cli.getOutputStream());
					serveurDIS = new DataInputStream(con_cli.getInputStream());
					System.out.println("Un client vient de se connecter!");
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
		}
	}

	/*
	 * Recupere le nom du fichier grace a la requete GET emise par le client
	 */
	public String receiveRequest()
	{
		String req = new String();
		try
		{
			req = serveurDIS.readUTF();
			System.out.println("Requete du client receptionnee.");
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			return "";
		}
		if(req.split(" ")[0].compareTo("GET") == 0)
			return req.split(" ")[1];
		else
			return "";
	}

	/*
	 * Envoi du contenu du fichier dont le nom est passe en parametre
	 */
	public void sendFile(String nomFichier)
	{
		if(nomFichier.compareTo("") != 0)
		{
			//Recuperation du fichier
			File f = new File("./src/projet_web/ServeurWeb/data/" + nomFichier);
			FileInputStream fis;
			try
			{
				//Creation du FileInputStream pour lire le fichier
				fis = new FileInputStream(f);
				//Ecriture de l'entete
				serveurDOS.writeUTF("HTTP/1.1 200 OK\n");
				serveurDOS.writeUTF("Content-Length: " + f.length() + "\n");
				serveurDOS.writeUTF("Content-Type: " + nomFichier.split("\\.")[1] + "\n");
				serveurDOS.writeUTF("Message_body: \n");
				serveurDOS.writeUTF("\n");
				//Lecture depuis le fichier et ecriture des donnees sur le flux
				int lect;
				while((lect = fis.read()) >= 0)
				{
					serveurDOS.write(lect);
				}
				fis.close();
				//Envoi du flux
				serveurDOS.flush();
				System.out.println("Contenu du fichier envoye au client!");
			}
			catch (FileNotFoundException e)
			{
				// TODO Auto-generated catch block
				System.out.println("Le fichier n'existe pas !");
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				System.out.println("Impossible de lire le fichier !");
			}
		}

	}

	/*
	 * Ferme la connexion etablie precedemment
	 */
	public void fermeConnexion()
	{
		if(con_cli != null)
		{
			try
			{
				serveurDOS.close();
				serveurDIS.close();
				con_cli.close();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				System.out.println("Erreur lors de la fermeture des sockets !");
			}
		}
	}

	public void fermeConnexionStop()
	{
		if(con_cli != null)
		{
			try
			{
				ss.close();
				serveurDOS.close();
				serveurDIS.close();
				con_cli.close();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				System.out.println("Erreur lors de la fermeture des sockets !");
			}
		}
	}

	/*
	 * Fonction effectuee par le serveur pour le lancer
	 */
	public void action()
	{
		while(true)
		{
			this.connexion();
			this.sendFile(this.receiveRequest());
			this.fermeConnexion();
		}

	}

}
