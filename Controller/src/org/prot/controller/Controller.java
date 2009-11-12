package org.prot.controller;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;

public class Controller
{
	private static final Logger logger = Logger.getLogger(Controller.class);
	
	private Server server;

	public void setServer(Server server)
	{
		this.server = server;
	}

	public void start()
	{
		try
		{
			logger.info("starting server"); 
			server.start();
		} catch (Exception e)
		{
			logger.error("Could not start the server", e);
			System.exit(1);
		}
	}

}
