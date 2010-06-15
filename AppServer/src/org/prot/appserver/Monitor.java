/*******************************************************************************
 * Copyright (c) 2010 Andreas Wolke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Wolke - initial API and implementation and initial documentation
 *******************************************************************************/
package org.prot.appserver;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpMethods;
import org.prot.util.ReservedAppIds;
import org.prot.util.scheduler.Scheduler;
import org.prot.util.scheduler.SchedulerTask;

/**
 * TODO: Use RMI to communicate with the Controller
 * 
 * @author Andreas Wolke
 * 
 */
public class Monitor extends SchedulerTask
{
	private static final Logger logger = Logger.getLogger(Monitor.class);

	private final int SLEEP_TIME = 3000;

	private final String CONTROLLER_HOST = "127.0.0.1";

	private final int CONTROLLER_PORT = 8080;

	public Monitor()
	{
		Scheduler.addTask(this);
	}

	@Override
	public long getInterval()
	{
		return SLEEP_TIME;
	}

	@Override
	public void run()
	{
		URL url;
		try
		{
			String sUrl = "http://" + CONTROLLER_HOST + ":" + CONTROLLER_PORT + "/" + ReservedAppIds.APP_PING
					+ "/";
			url = new URL(sUrl);

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod(HttpMethods.GET);
			connection.setUseCaches(false);

			connection.connect();

			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
			{
				logger.error("AppServer could not connect with the controller: " + sUrl);
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
			logger.error("Could not connect with the controller");
			System.exit(1);
		} catch (IOException e)
		{
			logger.error("IOException", e);
			System.exit(1);
		}
	}
}
