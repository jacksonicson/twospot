package org.prot.app.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerWrapper;

public class DosPreventionHandler extends HandlerWrapper
{
	private DosPrevention dosPrevention;

	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{
		long requestId = dosPrevention.startRequest();
		try
		{
			super.handle(target, baseRequest, request, response);
		} finally
		{
			dosPrevention.finishRequest(requestId);
		}

	}

	public void setDosPrevention(DosPrevention dosPrevention)
	{
		this.dosPrevention = dosPrevention;
	}
}
