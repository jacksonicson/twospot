package org.prot.frontend.handlers;

import java.io.IOException;
import java.util.logging.FileHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpMethods;
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
		if (baseRequest.getMethod().equals(HttpMethods.POST))
		{
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
					this.deployer.deployApplication(uri, baseRequest);
					return;
				}
			}
		}

		super.handle(target, baseRequest, request, response);
	}

	public void setDeployer(AppDeployer deployer)
	{
		this.deployer = deployer;
	}
}