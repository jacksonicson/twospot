package org.prot.util.handler;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;
import org.eclipse.jetty.http.HttpHeaders;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.server.HttpConnection;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.handler.AbstractHandler;

public abstract class HttpProxyHandler extends AbstractHandler
{
	private static final Logger logger = Logger.getLogger(HttpProxyHandler.class);

	private HttpClient httpClient;

	private Set<String> invalidHeaders = new HashSet<String>();

	public void init()
	{

		try
		{
			httpClient.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
			httpClient.setMaxRetries(0);
			httpClient.setMaxConnectionsPerAddress(1);
			httpClient.start();
		} catch (Exception e)
		{
			logger.error(e);
		}
	}

	protected String getUrl(Request request)
	{
		return null;
	}

	protected String getUri(Request request)
	{
		return null;
	}

	protected String getHost(Request request)
	{
		return null;
	}

	protected void setupInvalidHeaders()
	{
		invalidHeaders.add(HttpHeaders.CONNECTION);
		invalidHeaders.add(HttpHeaders.ACCEPT_ENCODING);
		invalidHeaders.add(HttpHeaders.VIA);
		invalidHeaders.add(HttpHeaders.FORWARDED);
	}

	protected boolean isFilteredHeader(String header, Set<String> invalidTempHeaders)
	{
		return invalidHeaders.contains(header) || invalidTempHeaders.contains(header);
	}

	protected void forwardRequest(Request baseRequest, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws Exception
	{
		HttpConnection httpConnection = HttpConnection.getCurrentConnection();
		Request srcRequest = httpConnection.getRequest();

		// Generate the URL to the destination Server
		String url = getUrl(srcRequest);
		String uri = getUri(srcRequest);
		String host = getHost(srcRequest);

		forwardRequest(baseRequest, httpRequest, httpResponse, url, uri, host);
	}

	class MyExchange extends HttpExchange
	{
		private HttpServletResponse response;
		private HttpServletRequest request; 

		public MyExchange(HttpServletResponse response, HttpServletRequest request)
		{
			this.response = response;
			this.request = request;
		}

		@Override
		public void onConnectionFailed(Throwable ex)
		{
			try
			{
				response.sendError(503, "connection failed");
			} catch (IOException e)
			{
				logger.error(e);
			}
		}

		@Override
		public void onException(Throwable ex)
		{
			logger.error(ex);
		}

		@Override
		public void onExpire()
		{
			try
			{
				response.sendError(404, "Expired");
			} catch (IOException e)
			{
				logger.error(e);
			}
		}

		@Override
		public void onResponseComplete() throws IOException
		{
			// response.flushBuffer();
			// response.getOutputStream().close();
			((Request)request).setHandled(true);
			System.out.println("resume the continuation");
			Continuation cont = ContinuationSupport.getContinuation(request);
			cont.complete(); 
		}

		@Override
		public void onResponseContent(Buffer buffer) throws IOException
		{
			buffer.writeTo(response.getOutputStream());
		}

		@Override
		public void onResponseHeader(Buffer name, Buffer value) throws IOException
		{
			response.addHeader(name.toString(), value.toString());
		}

		@Override
		public void onResponseHeaderComplete() throws IOException
		{
			// Add proxy specific headers
			// response.addHeader("via", "http localhost");
			// response.addHeader("x-forwarded-for", "");
			// response.addHeader("x-forwarded-host", "");
			// response.addHeader("x-forwarded-server", "");
			// response.addHeader("accept-encoding", "");
		}

		@Override
		public void onResponseStatus(Buffer version, int status, Buffer reason) throws IOException
		{
			response.setStatus(status);
		}
	}

	protected void forwardRequest(Request baseRequest, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse, String url, String uri, String host) throws Exception
	{
		// Temporary invalid headers
		Set<String> invalidTempHeaders = new HashSet<String>();

		// Get original request and response objects
		HttpConnection httpConnection = HttpConnection.getCurrentConnection();
		Request srcRequest = httpConnection.getRequest();
		Response response = httpConnection.getResponse();

		// Generate the new request
		MyExchange exchange = new MyExchange(httpResponse, httpRequest);
		exchange.setRetryStatus(false);
		exchange.setMethod(srcRequest.getMethod());

		exchange.setURL(url);
		exchange.setURI(uri);

		if (srcRequest.getContentType() != null)
			exchange.setRequestContentType(srcRequest.getContentType());

		// Handle specific requests
		// Connection request

		String connectionToken = srcRequest.getHeader("Connection");
		if (connectionToken != null)
		{
			invalidTempHeaders.add(connectionToken);
		}

		// Copy all headers to the exchange
		for (Enumeration<String> headers = srcRequest.getHeaderNames(); headers.hasMoreElements();)
		{
			String name = headers.nextElement();
			String value = srcRequest.getHeader(name);

			// Filter all invalid headers
			if (isFilteredHeader(name, invalidTempHeaders))
				continue;

			exchange.setRequestHeader(name, value);
		}

		// Changed headers
		exchange.setRequestHeader(HttpHeaders.HOST, host);

		// Copy the request content
		if (srcRequest.getContentLength() > 0)
			exchange.setRequestContentSource(srcRequest.getInputStream());

		// send the request
		try
		{
			Continuation continuation = ContinuationSupport.getContinuation(srcRequest);
			continuation.suspend();

			httpClient.send(exchange);

		} catch (Exception e)
		{
			logger.error("Error while sending proxy request", e);
		}
	}

	public void setHttpClient(HttpClient httpClient)
	{
		this.httpClient = httpClient;
	}
}
