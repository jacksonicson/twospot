package org.prot.frontend.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.prot.manager.exceptions.MissingControllerException;
import org.prot.manager.pojos.AppServerInfo;
import org.prot.manager.service.frontend.FrontendService;
import org.prot.util.handler.HttpProxyHandler;

public class FlowHandler extends HttpProxyHandler
{
	private FrontendService frontendService; 

	private ThreadLocal<AppServerInfo> info = new ThreadLocal<AppServerInfo>();
	
	public FlowHandler() {
		setup(); 
	}
	
	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{

		try
		{
			AppServerInfo info = frontendService.chooseAppServer("TODO");
			this.info.set(info); 
		} catch (MissingControllerException e1)
		{
			e1.printStackTrace();
		} 
		
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
		return this.info.get().getControllerAddress();
	}

	@Override
	protected String getUri(Request request)
	{
		return request.getUri().toString();
	}

	@Override
	protected String getUrl(Request request)
	{
		return "http://" + this.info.get().getControllerAddress();
	}


}
