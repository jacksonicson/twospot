package org.prot.frontend.handlers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.server.Request;
import org.prot.manager.service.frontend.FrontendService;
import org.prot.util.handler.HttpProxyHandler;

public class FlowHandler extends HttpProxyHandler
{
	private FrontendService frontendService; 
	
	public FlowHandler() {
		setup(); 
	}
	
	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{

//		System.out.println("Flow Handler"); 
//		frontendService.chooseAppServer("helloworld"); 
		
		try
		{
			forwardRequest(baseRequest, request, response);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void setFrontendService(FrontendService frontendService)
	{
		this.frontendService = frontendService;
	}

	@Override
	protected String getHost(Request request)
	{
		return "www.heise.de";
	}

	@Override
	protected String getUri(Request request)
	{
		return request.getUri().toString();
	}

	@Override
	protected String getUrl(Request request)
	{
		return "http://www.heise.de";
	}


}
