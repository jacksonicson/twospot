package org.prot.frontend.handlers;

import java.io.IOException;
import java.util.logging.FileHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpMethods;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.prot.frontend.deploy.AppDeployer;

public class FilterHandler extends ProxyHandler
{
	private static final Logger logger = Logger.getLogger(FileHandler.class);

	private AppDeployer deployer;

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{
		// Check the request type
		if (baseRequest.getMethod().equals(HttpMethods.POST))
		{
			// TODO: There are multiple code duplicates of this -> make it more general 
			String uri = request.getRequestURI();
			if (uri.startsWith("/"))
				uri = uri.substring(1);

			int index = -1;

			if (uri.indexOf("deploy") == 0)
			{
				uri = uri.substring("deploy".length() + 1);

				index = uri.indexOf('/');
				if (index > 0)
					uri = uri.substring(0, index);

				if (!uri.equals(""))
				{
					logger.debug("Deploying appId: " + uri);
					try
					{
						this.deployer.deployApplication(uri, baseRequest);
						response.setStatus(HttpStatus.OK_200);
						baseRequest.setHandled(true); 
						
					} catch (InterruptedException e)
					{
						response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500); 
						baseRequest.setHandled(true); 
						return; 
					}
					
					return;
				}
			}
		}

		// Could not handle the request
		super.handle(target, baseRequest, request, response);
	}

	public void setDeployer(AppDeployer deployer)
	{
		this.deployer = deployer;
	}
}
