package org.prot.frontend.deploy;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.jetty.client.Address;
import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.http.HttpMethods;
import org.eclipse.jetty.http.HttpSchemes;
import org.eclipse.jetty.server.Request;
import org.prot.manager.services.FrontendService;

public class AppDeployer
{
	private static final Logger logger = Logger.getLogger(AppDeployer.class);
	
	private HttpClient httpClient;
	
	private FrontendService frontendService; 

	public void deployApplication(String appId, Request request)
	{
		// Write everything to the FileServer
		ContentExchange exchange = new ContentExchange();
		exchange.setMethod(HttpMethods.POST);
		exchange.setScheme(HttpSchemes.HTTP_BUFFER);
		exchange.setAddress(new Address("localhost", 5050)); // TODO: Configure
		exchange.setURI("/" + appId + "/null"); // TODO: Configure
		try
		{
			exchange.setRequestContentSource(request.getInputStream());
		} catch (IOException e)
		{
			// TODO: Error handling
			e.printStackTrace();
		}
		
		int status = 0; 
		try
		{
			httpClient.send(exchange);
			status = exchange.waitForDone();
			status = exchange.getResponseStatus(); 
		} catch (IOException e)
		{
			// TODO: Error handling
			e.printStackTrace(); 
		} catch (InterruptedException e)
		{
			// TODO: Error handling
			e.printStackTrace();
		}
		
		logger.info("calling manager"); 
		frontendService.newAppOrVersion(appId);
	}

	
	public void setHttpClient(HttpClient httpClient)
	{
		this.httpClient = httpClient;
	}

	public void setFrontendService(FrontendService frontendService)
	{
		this.frontendService = frontendService;
	}
}
