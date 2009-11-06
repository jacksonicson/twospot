package org.prot.util.handler;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpFields.Field;
import org.eclipse.jetty.io.EofException;
import org.eclipse.jetty.server.HttpConnection;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.handler.AbstractHandler;

public abstract class HttpProxyHandler extends AbstractHandler
{
	private static final Logger logger = Logger.getLogger(HttpProxyHandler.class);

	private HttpClient httpClient;
	
	private Set<String> invalidHeaders = new HashSet<String>();
	private Set<String> invalidTempHeaders = new HashSet<String>(); 

	public void setup()
	{
		// Setup the httpClient
		httpClient = new HttpClient();
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
		
		// Setup the list of invalid headers
		setupInvalidHeaders(); 
	}
	

	protected abstract String getUrl(Request request);
	protected abstract String getUri(Request request); 
	protected abstract String getHost(Request request); 

	
	protected void setupInvalidHeaders() {
		invalidHeaders.add("Connection");
		invalidHeaders.add("Host");
		invalidHeaders.add("KeepAlive");
		invalidHeaders.add("TransferEncoding");
		invalidHeaders.add("Trailer");
		invalidHeaders.add("ProxyAuthorization");
		invalidHeaders.add("ProxyAuthenticate");
		invalidHeaders.add("Proxy-Connection");
		invalidHeaders.add("Upgrade");
	}

	protected boolean isFilteredHeader(String header)
	{
		return invalidHeaders.contains(header) || invalidTempHeaders.contains(header);  
	}

	protected void onConnectionFailure()
	{
		// do nothing
	}

	private void preProcess()
	{
		invalidTempHeaders.clear(); 
	}
	
	protected void forwardRequest(Request baseRequest, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws Exception
	{
		// TODO: Handle Connect-Requests
		
		// Prepare object state for processing this request
		preProcess(); 
		
		// Get original request and response objects
		HttpConnection httpConnection = HttpConnection.getCurrentConnection();
		Request srcRequest = httpConnection.getRequest();
		Response srcResponse = httpConnection.getResponse();

		// Generate the URL to the destination Server
		String url = getUrl(srcRequest);
		String uri = getUri(srcRequest); 
		String host = getHost(srcRequest);

		// Generate the new request
		ContentExchange exchange = new ContentExchange(true);
		exchange.setRetryStatus(false);
		exchange.setMethod(srcRequest.getMethod());
		exchange.setURL(url); 
		exchange.setURI(uri); 
//		exchange.setRequestContentType(srcRequest.getContentType());

		// Fill specific headers
		exchange.setRequestHeader("Host", host);
		
		// Handle specific requests
		// Connection request
		String connectionToken = srcRequest.getHeader("Connection"); 
		if(connectionToken == null)
			connectionToken = null; 
		else
			if(connectionToken.equalsIgnoreCase("keepalive") || connectionToken.equalsIgnoreCase("close"))
				connectionToken = null;
		
		if(connectionToken != null)
			invalidTempHeaders.add(connectionToken); 
		

		// Copy all headers to the exchange
		for (Enumeration<String> headers = srcRequest.getHeaderNames(); headers.hasMoreElements();)
		{
			String name = headers.nextElement();
			String value = srcRequest.getHeader(name);

			// Filter all invalid headers
			if (isFilteredHeader(name))
				continue;

			exchange.setRequestHeader(name, value);
		}
		
		// Copy the request content
		try
		{
			exchange.setRequestContentSource(srcRequest.getInputStream());
		} catch (IOException e)
		{
			logger.error("cannot copy request content", e);
			throw e;
		}

		
		// Request generation done
		try
		{
			// send the request
			httpClient.send(exchange);

			// wait until response is complete
			exchange.waitForDone();

		} catch (InterruptedException e)
		{
			logger.info("reporting a stale appserver");
			onConnectionFailure();
			throw e;

		} catch (IOException e)
		{
			logger.error("reporting a stale appserver");
			onConnectionFailure();
			throw e;
		}
		

		// Handle response status
		switch (exchange.getStatus())
		{
		case HttpExchange.STATUS_EXPIRED:
		case HttpExchange.STATUS_EXCEPTED:
			logger.error("reporting a stale appserver");
			onConnectionFailure();
			return; // TODO: Throw an exception
		}
		

		// Create the response
		try
		{
			// fill response
			srcResponse.setStatus(exchange.getResponseStatus());

			// Copy all headers
			HttpFields fields = exchange.getResponseFields();
			for (int i = 0; i < fields.size(); i++)
			{
				Field field = fields.getField(i);
				srcResponse.setHeader(field.getName(), field.getValue());

				logger.debug(field.getName() + ":" + field.getValue());
			}
			
			// Add additional headers
			srcResponse.setHeader("Via", "0.0 TODO");

			if (exchange.getResponseContentBytes() != null) {
				srcResponse.getOutputStream().write(exchange.getResponseContentBytes()); 
				srcResponse.getOutputStream().close();
			}

		} catch (EofException e)
		{
			// Client closed the connection
		} catch (IOException e)
		{
			// Client closed the connection
		}
	}
}
