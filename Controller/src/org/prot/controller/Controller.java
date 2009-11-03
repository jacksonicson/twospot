package org.prot.controller;

import org.eclipse.jetty.server.Server;

public class Controller
{
	private Server server;

	public void setServer(Server server)
	{
		this.server = server;
	}

	public void start()
	{
		try
		{
			server.start();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
