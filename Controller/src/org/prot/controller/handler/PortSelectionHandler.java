package org.prot.controller.handler;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class PortSelectionHandler extends AbstractHandler
{
	private static final Logger logger = Logger.getLogger(PortSelectionHandler.class);
	
	private Hashtable<Integer, Handler> handlers = new Hashtable<Integer, Handler>();

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{
		// get port info
		int port = baseRequest.getServerPort();

		// forward call
		Handler handler = handlers.get(port);
		if (handler != null)
		{
			handler.handle(target, baseRequest, request, response);
			return;
		}
		
		// error
		logger.error("Accessing Controller on an invalid port"); 
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		response.sendError(HttpServletResponse.SC_NOT_FOUND);
	}

	public void setHandlers(List<HandlerDecorator> decorators)
	{
		for (HandlerDecorator decorator : decorators)
		{
			this.handlers.put(decorator.getPort(), decorator.getHandler());
		}
	}
}
