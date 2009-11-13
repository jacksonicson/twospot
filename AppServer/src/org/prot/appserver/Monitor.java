package org.prot.appserver;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpMethods;

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
			URL url;
			try
			{
				url = new URL("http://" + CONTROLLER_HOST + ":" + CONTROLLER_PORT);

				HttpURLConnection connection = (HttpURLConnection) url.openConnection();

				connection.setRequestMethod(HttpMethods.GET);
				connection.setUseCaches(false);

				connection.connect();

				if (connection.getResponseCode() != HttpURLConnection.HTTP_NOT_FOUND)
				{
					logger.error("AppServer could not connect with the controller");
					System.exit(1);
				}

			} catch (MalformedURLException e)
			{
				logger.error("MaleformedURLException", e);
				System.exit(1);
			} catch (ProtocolException e)
			{
				logger.error("Protocol exception", e);
				System.exit(1);
			} catch (ConnectException e)
			{
				logger.error("Could not connect to the controller");
				System.exit(1);
			} catch (IOException e)
			{
				logger.error("IOException", e);
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
