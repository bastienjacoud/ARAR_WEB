package projet_web;

import java.io.IOException;
import java.net.ServerSocket;

public class ServeurWeb {

	ServerSocket ss;

	public ServeurWeb()
	{
		try
		{
			ss = new ServerSocket(1234);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
