package org.prot.appserver.runtime.java;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;

public class ErrorHandler extends org.eclipse.jetty.server.handler.ErrorHandler
{
	private static final Logger logger = Logger.getLogger(ErrorHandler.class);

	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException

	{
		logger.error("Error handler on: " + request.getRequestURL());
		super.handle(target, baseRequest, request, response);
	}
}