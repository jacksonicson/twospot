package org.prot.httpfileserver.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class AnnounceUploadHandler extends AbstractHandler
{
	private static final Logger logger = Logger.getLogger(AnnounceUploadHandler.class);

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{
		logger.debug("Announcing upload");

		if(baseRequest.isHandled())
			return;
		
		response.getWriter().print("" + System.currentTimeMillis());
		baseRequest.setHandled(true);
	}
}
