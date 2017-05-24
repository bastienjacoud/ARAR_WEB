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
			System.out.println("test1");
			con_cli = ss.accept();
			System.out.println("test2");
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
			return "";
		}
		return req.split(" ")[1];
	}

	public void sendFile(String nomFichier)
	{
		File f = new File(nomFichier);
		FileInputStream fis;
		//FileOutputStream fos;
		byte[] buf = new byte[2056];
		try
		{
			fis = new FileInputStream(f);
			int i=0;
			while(fis.read(buf) >= 0)
			{
				i++;
			}
			String msgEnTete = new String("HTTP/1.1 200 OK\nContent-Length: " + i + "\nContent-Type: " + nomFichier.split(".")[1] + "\nMessage_body:");
			serveurDOS.write(msgEnTete.getBytes());
			serveurDOS.write(buf);
			serveurDOS.write("\n".getBytes());
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
			System.out.println("Erreur lors de l'envoi des donnï¿½es du fichier !");
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

	public void action()
	{
		this.connexion();
		System.out.println("Connexion établie.");
		this.sendFile(this.receiveRequest());
		this.fermeConnexion();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		new ServeurWeb().action();
	}

}
