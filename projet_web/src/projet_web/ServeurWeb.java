package projet_web;

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

	public ServeurWeb()
	{
		try
		{
			ss = new ServerSocket(1234);
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

	public void connexion()
	{
		try
		{
			con_cli = ss.accept();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(con_cli != null)
		{
			try
			{
				serveurDOS = new DataOutputStream(con_cli.getOutputStream());
				serveurDIS = new DataInputStream(con_cli.getInputStream());
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public String receiveRequest()
	{
		String req = new String();
		try
		{
			req = serveurDIS.readUTF();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return req.split(" ")[1];
	}

	public void sendFile(String nomFichier)
	{
		File f = new File(nomFichier);
		FileInputStream fis;
		//FileOutputStream fos;
		byte[] buf = new byte[2056];

		String msgEnTete = new String("HTTP/1.1 200 OK\nMessage_body:");
		try
		{
			serveurDOS.write(msgEnTete.getBytes());
		}
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			System.out.println("Erreur lors de l'écriture de l'en tete !");
		}
		try
		{
			fis = new FileInputStream(f);
			while(fis.read(buf) >= 0)
			{

			}
			serveurDOS.write(buf);
			fis.close();
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
		try
		{
			serveurDOS.flush();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			System.out.println("Erreur lors de l'envoi des données du fichier !");
		}
	}

	public void fermeConnexion()
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
