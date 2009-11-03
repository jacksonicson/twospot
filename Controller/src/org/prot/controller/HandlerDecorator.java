package org.prot.controller;

import org.eclipse.jetty.server.Handler;

public class HandlerDecorator
{
	private Handler handler; 
	private int port;
	public Handler getHandler()
	{
		return handler;
	}
	public void setHandler(Handler handler)
	{
		this.handler = handler;
	}
	public int getPort()
	{
		return port;
	}
	public void setPort(int port)
	{
		this.port = port;
	}
}
