package org.prot.appserver.management;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

public class ManagementHandler extends ContextHandlerCollection
{
	private Handler handler;

	private Management management;

	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{
		this.management.handleConnection();
		super.handle(target, baseRequest, request, response);
	}

	public void setHandler(Handler handler)
	{
		this.handler = handler;
	}

	public void setManagement(Management management)
	{
		this.management = management;
	}
}
