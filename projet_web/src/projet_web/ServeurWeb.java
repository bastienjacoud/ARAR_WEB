package projet_web;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServeurWeb {

	ServerSocket ss;
	Socket con_cli;

	public ServeurWeb()
	{
		try
		{
			ss = new ServerSocket(1234);
			con_cli = new Socket();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
