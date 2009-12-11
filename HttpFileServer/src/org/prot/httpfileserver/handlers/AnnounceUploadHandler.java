package org.prot.httpfileserver.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpMethods;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class AnnounceUploadHandler extends AbstractHandler
{
	private static final Logger logger = Logger.getLogger(AnnounceUploadHandler.class);

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{
		if (baseRequest.isHandled())
			return;

		if (baseRequest.getMethod().equals(HttpMethods.GET) == false)
			return;

		String uri = baseRequest.getRequestURI();
		if (uri.startsWith("/"))
			uri = uri.substring(0);

		if (uri.indexOf("announce") != -1)
		{
			String token = "" + System.currentTimeMillis();
			logger.debug("Announced upload token is " + token);

			response.getWriter().print(token);
			response.setStatus(HttpStatus.OK_200);
			baseRequest.setHandled(true);
		}
	}
}
