package org.prot.util.handler;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpDestination;
import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.http.HttpHeaders;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.server.HttpConnection;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.thread.ThreadPool;

public abstract class HttpProxyHandler extends AbstractHandler
{
	private static final Logger logger = Logger.getLogger(HttpProxyHandler.class);

	private HttpClient httpClient;

	private Set<String> invalidHeaders = new HashSet<String>();

	private Set<String> invalidTempHeaders = new HashSet<String>();

	private ThreadPool threadPool = null;

	public void setThreadPool(ThreadPool threadPool)
	{
		this.threadPool = threadPool;
	}

	public void setup()
	{
		// Setup the httpClient
		httpClient = new HttpClient();
		httpClient.setThreadPool(threadPool);
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

		public MyExchange(HttpServletResponse response)
		{
			this.response = response;
		}

		@Override
		public void onConnectionFailed(Throwable ex)
		{
			try
			{
				response.sendError(503, "connection failed");
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		@Override
		public void onException(Throwable ex)
		{
			ex.printStackTrace();
		}

		@Override
		public void onExpire()
		{
			try
			{
				response.sendError(404, "Expired");
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		@Override
		public void onResponseComplete() throws IOException
		{
			// response.flushBuffer();
			// response.getOutputStream().close();
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
		// Prepare object state for processing this request
		preProcess();

		// Get original request and response objects
		HttpConnection httpConnection = HttpConnection.getCurrentConnection();
		Request srcRequest = httpConnection.getRequest();
		Response response = httpConnection.getResponse();

		// Generate the new request
		MyExchange exchange = new MyExchange(httpResponse);
		exchange.setRetryStatus(false);

		exchange.setMethod(srcRequest.getMethod());

		exchange.setURL(url);
		exchange.setURI(uri);

		if (srcRequest.getContentType() != null)
			exchange.setRequestContentType(srcRequest.getContentType());

		// Handle specific requests
		// Connection request
		String connectionToken = srcRequest.getHeader("Connection");
		if (connectionToken == null)
			connectionToken = null;
		else if (connectionToken.equalsIgnoreCase("keepalive") || connectionToken.equalsIgnoreCase("close"))
			connectionToken = null;

		if (connectionToken != null)
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
		if (srcRequest.getContentLength() > 0)
			exchange.setRequestContentSource(srcRequest.getInputStream());

		// Request generation done
		try
		{
			// send the request
			httpClient.send(exchange);

			// wait until response is complete
			int status = exchange.waitForDone();

			// TODO: Handle status

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
	}
}
