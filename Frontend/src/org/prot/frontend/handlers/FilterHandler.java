package org.prot.frontend.handlers;

import java.io.IOException;
import java.util.logging.FileHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.http.HttpMethods;
import org.eclipse.jetty.server.Request;
import org.prot.frontend.deploy.AppDeployer;
import org.prot.manager.services.FrontendService;

public class FilterHandler extends ProxyHandler
{
	private static final Logger logger = Logger.getLogger(FileHandler.class);

	HttpClient httpClient;
	AppDeployer deployer;

	public FilterHandler()
	{
		httpClient = new HttpClient();
		httpClient.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
		try
		{
			httpClient.start();
		} catch (Exception e)
		{
			logger.fatal("could not start the HttpClient", e);
			System.exit(1);
		}

		deployer = new AppDeployer();
		deployer.setHttpClient(httpClient);
	}
	
	public void init() {
		deployer.setFrontendService(frontendService);
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{
		logger.info("testing method"); 
		if (baseRequest.getMethod().equals(HttpMethods.POST))
		{
			String uri = request.getRequestURI().toString().substring(1);
			logger.info("post: " + uri); 
			if (uri.indexOf("deploy") == 0)
			{
				uri = uri.substring("deploy".length() + 1);
				uri.replaceAll("/", "");
				String appId = uri;

				logger.info("deploying application with the appid: " + appId);
				deployer.deployApplication(appId, baseRequest);
				
				baseRequest.setHandled(true);
				return;
			}
		}

		super.handle(target, baseRequest, request, response);
	}
}
