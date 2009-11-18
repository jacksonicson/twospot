package org.prot.frontend.handlers;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.FileHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpMethods;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.prot.frontend.deploy.AppDeployer;
import org.prot.frontend.deploy.login.LoginHandler;
import org.prot.util.AppIdExtractor;

public class FilterHandler extends ProxyHandler
{
	private static final Logger logger = Logger.getLogger(FileHandler.class);

	private AppDeployer deployer;

	private LoginHandler loginHandler;

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{
		// Check the request type
		if (baseRequest.getMethod().equals(HttpMethods.POST))
		{
			// TODO: There are multiple code duplicates of this -> make it more
			// general
			String uri = request.getRequestURI();
			if (uri.startsWith("/"))
				uri = uri.substring(1);

			if (uri.indexOf("deploy") == 0)
			{
				uri = uri.substring("deploy".length());

				// Extract the AppId
				String appId = AppIdExtractor.fromUri(uri);
				if (appId != null)
				{
					try
					{
						logger.debug("Deploying appId: " + uri);
						this.deployer.deployApplication(appId, baseRequest);

						// Everything is ok
						response.setStatus(HttpStatus.OK_200);
						baseRequest.setHandled(true);

					} catch (InterruptedException e)
					{
						// Respnod with an error
						response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
						baseRequest.setHandled(true);
						return;
					}

					return;
				}
			}
		} else if (baseRequest.getMethod().equals(HttpMethods.GET))
		{
			String uri = request.getRequestURI();
			if (uri.startsWith("/"))
				uri = uri.substring(1);

			logger.info("Uri: " + uri);

			if (uri.indexOf("login") == 0)
			{
				try
				{
					String appId = request.getParameter("appid");
					if (appId != null)
					{

						String key = loginHandler.doLogin(request.getParameter("appid"), request
								.getParameter("username"), request.getParameter("password"));

						if (key == null)
						{
							logger.info("ERROR");
							response.sendError(HttpStatus.UNAUTHORIZED_401,
									"Could not verify user credentials");
							baseRequest.setHandled(true);
							return;
						}

						logger.info("OK");

						response.sendRedirect("http://www.andmedia.de?key=" + key);
						baseRequest.setHandled(true);
						return;
					}

				} catch (NoSuchAlgorithmException e)
				{
					logger.error("Could not MD5 encode user password", e);
					response.sendError(HttpStatus.INTERNAL_SERVER_ERROR_500,
							"Could not MD5 encode user password");
					baseRequest.setHandled(true);
					return;
				}
			}
		}
		logger.info("test");
		// Could not handle the request
		super.handle(target, baseRequest, request, response);
	}

	public void setDeployer(AppDeployer deployer)
	{
		this.deployer = deployer;
	}

	public void setLoginHandler(LoginHandler loginHandler)
	{
		this.loginHandler = loginHandler;
	}
}
