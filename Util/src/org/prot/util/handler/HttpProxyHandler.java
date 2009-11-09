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
			ex.printStackTrace();
		}

		@Override
		public void onException(Throwable ex)
		{
			// TODO Auto-generated method stub
			ex.printStackTrace();
		}

		@Override
		public void onExpire()
		{
			// TODO Auto-generated method stub
			System.out.println("expired");
		}

		@Override
		public void onRequestCommitted() throws IOException
		{
			// TODO Auto-generated method stub
		}

		@Override
		public void onRequestComplete() throws IOException
		{
			// TODO Auto-generated method stub
		}

		@Override
		public void onResponseComplete() throws IOException
		{
			// TODO Auto-generated method stub
			response.flushBuffer();
			// response.getOutputStream().close();
			// System.out.println("close");
		}

		@Override
		public void onResponseContent(Buffer b) throws IOException
		{
			// System.out.println("content: " + content.toString());
			// String content = b.toString();
			// System.out.println("Content: " + content);
			// response.getOutputStream().write(content.getBytes());
			b.writeTo(response.getOutputStream());
		}

		@Override
		public void onResponseHeader(Buffer name, Buffer value) throws IOException
		{
			// System.out.println("header: " + name + " value: " + value);
			response.addHeader(name.toString(), value.toString());
		}

		@Override
		public void onResponseHeaderComplete() throws IOException
		{
			// System.out.println("header complete");

		}

		@Override
		public void onResponseStatus(Buffer version, int status, Buffer reason) throws IOException
		{
			// System.out.println("status: " + status);
			response.setStatus(status, reason.toString());
		}

		@Override
		public void onRetry()
		{
			// System.out.println("retry");
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

		// System.out.println("url: " + url);
		// System.out.println("uri: " + uri);

		// exchange.setRequestContentType(srcRequest.getContentType());

		// Fill specific headers
		exchange.setRequestHeader("Host", host);

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

			// System.out.println("-> name: " + name + " value: " + value);

			// Filter all invalid headers
			if (isFilteredHeader(name))
				continue;

			exchange.setRequestHeader(name, value);
		}

		// System.out.println("Content Length: " +
		// srcRequest.getContentLength());

		// Copy the request content
		try
		{
			// Only!!! if there is content to write!
			if (srcRequest.getContentLength() > 0)
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
			// System.out.println("sending");
			httpClient.send(exchange);

			// wait until response is complete
			exchange.waitForDone();
			// System.out.println("its done");

			// TODO: Handle errors

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

		// response.getOutputStream().print("FLKJDSFKDSLFJ");

		// // Handle response status
		// switch (exchange.getStatus())
		// {
		// case HttpExchange.STATUS_EXPIRED:
		// case HttpExchange.STATUS_EXCEPTED:
		// logger.error("reporting a stale appserver");
		// onConnectionFailure();
		// return; // TODO: Throw an exception
		// }
		//
		// // Create the response
		// try
		// {
		// // fill response
		// srcResponse.setStatus(exchange.getResponseStatus());
		//
		// // Copy all headers
		// HttpFields fields = exchange.getResponseFields();
		// for (int i = 0; i < fields.size(); i++)
		// {
		// Field field = fields.getField(i);
		// srcResponse.setHeader(field.getName(), field.getValue());
		//
		// logger.debug(field.getName() + ":" + field.getValue());
		// }
		//
		// // Add additional headers
		// srcResponse.setHeader("Via", "0.0 TODO");
		//
		// if (exchange.getResponseContentBytes() != null)
		// {
		// srcResponse.getOutputStream().write(exchange.getResponseContentBytes());
		// srcResponse.getOutputStream().close();
		// }
		//
		// } catch (EofException e)
		// {
		// // Client closed the connection
		// } catch (IOException e)
		// {
		// // Client closed the connection
		// }
	}
}
