package org.prot.appserver;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;

/**
 * TODO: Use RMI to communicate with the Controller
 * 
 * @author Andreas Wolke
 * 
 */
public class Monitor extends Thread
{
	private static final Logger logger = Logger.getLogger(Monitor.class);

	private final int sleepTime = 3000;

	private final String CONTROLLER_HOST = "127.0.0.1";

	private final int CONTROLLER_PORT = 8080;

	public Monitor()
	{
		try
		{
			this.start();
		} catch (Exception e)
		{
			logger.error("", e);
			System.exit(1);
		}
	}

	public void run()
	{
		while (true)
		{
			try
			{
				URL controller = new URL("HTTP", CONTROLLER_HOST, CONTROLLER_PORT, "");
				URLConnection connection = controller.openConnection();

				DataInputStream in = new DataInputStream(connection.getInputStream());
				in.read();
				in.close();

			} catch (IOException e)
			{
				logger.error("", e);
				System.exit(1);
			}

			try
			{
				sleep(sleepTime);
			} catch (InterruptedException e)
			{
				logger.error("", e);
				System.exit(1);
			}
		}
	}
}
