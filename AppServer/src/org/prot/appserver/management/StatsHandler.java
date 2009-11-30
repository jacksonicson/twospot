package org.prot.appserver.management;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.prot.util.managment.RequestStats;

public class StatsHandler extends HandlerWrapper
{
	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{
		RequestStats.countRequest();
		
		super.handle(target, baseRequest, request, response);
	}
}
