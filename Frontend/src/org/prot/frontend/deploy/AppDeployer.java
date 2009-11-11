package org.prot.frontend.deploy;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.jetty.client.Address;
import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.http.HttpMethods;
import org.eclipse.jetty.http.HttpSchemes;
import org.eclipse.jetty.server.Request;

public class AppDeployer
{
	private static final Logger logger = Logger.getLogger(AppDeployer.class);
	
	private HttpClient httpClient;

	public void deployApplication(String appId, Request request)
	{
		// Write everything into the FileServer
		ContentExchange exchange = new ContentExchange();
		exchange.setMethod(HttpMethods.POST);
		exchange.setScheme(HttpSchemes.HTTP_BUFFER);
		exchange.setAddress(new Address("localhost", 5050)); // TODO: Configure
		exchange.setURI("/" + appId); // TODO: Configure
		try
		{
			exchange.setRequestContentSource(request.getInputStream());
		} catch (IOException e)
		{
			// TODO: Error handling
			e.printStackTrace();
		}
		
		try
		{
			httpClient.send(exchange);
			int status = exchange.waitForDone(); 
			logger.info("Deployment status: " + exchange.getResponseStatus()); 
			
		} catch (IOException e)
		{
			e.printStackTrace(); 
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		} 

		// Register or update Application in the manager

		// Register Application

	}

	public void setHttpClient(HttpClient httpClient)
	{
		this.httpClient = httpClient;
	}
}
