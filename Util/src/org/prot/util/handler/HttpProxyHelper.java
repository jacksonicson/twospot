package org.prot.util.handler;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;
import org.eclipse.jetty.http.HttpHeaderValues;
import org.eclipse.jetty.http.HttpHeaders;
import org.eclipse.jetty.http.HttpSchemes;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.TypeUtil;

public class HttpProxyHelper
{
	private static final Logger logger = Logger.getLogger(HttpProxyHelper.class);

	private HttpClient httpClient;

	private final Set<String> invalidHeaders = new HashSet<String>();

	private final List<ExceptionListener> exceptionListeners = new ArrayList<ExceptionListener>();

	protected void setupInvalidHeaders()
	{
		invalidHeaders.add(HttpHeaders.PROXY_CONNECTION);
		invalidHeaders.add(HttpHeaders.CONNECTION);
		invalidHeaders.add(HttpHeaders.KEEP_ALIVE);
		invalidHeaders.add(HttpHeaders.TRANSFER_ENCODING);
		invalidHeaders.add(HttpHeaders.TE);
		invalidHeaders.add(HttpHeaders.TRAILER);
		invalidHeaders.add(HttpHeaders.PROXY_AUTHORIZATION);
		invalidHeaders.add(HttpHeaders.PROXY_AUTHENTICATE);
		invalidHeaders.add(HttpHeaders.UPGRADE);
	}

	public void addExceptionListener(ExceptionListener listener)
	{
		synchronized (exceptionListeners)
		{
			exceptionListeners.add(listener);
		}
	}

	private void fireException(Throwable e, Object obj)
	{
		List<ExceptionListener> copy = new ArrayList<ExceptionListener>();
		synchronized (exceptionListeners)
		{
			copy.addAll(exceptionListeners);
		}

		for (ExceptionListener listener : copy)
			listener.onException(e, obj);
	}

	protected boolean isFilteredHeader(String header)
	{
		return invalidHeaders.contains(header);
	}

	public void forwardRequest(final Request jetRequest, final HttpServletRequest request,
			final HttpServletResponse response, final HttpURI url) throws Exception
	{
		forwardRequest(jetRequest, request, response, url, null);
	}

	public void forwardRequest(final Request jetRequest, final HttpServletRequest request,
			final HttpServletResponse response, final HttpURI url, final Object obj) throws Exception
	{
		if (url == null)
			throw new NullPointerException("URL must not be null");

		// Check if its a CONNECT request
		if (request.getMethod().equalsIgnoreCase("CONNECT"))
		{
			// TODO: Implement this
			response.sendError(HttpStatus.NOT_IMPLEMENTED_501);
			jetRequest.setHandled(true);
			return;
		}

		// Get the input and output streams
		final InputStream in = request.getInputStream();
		final OutputStream out = response.getOutputStream();

		// Get a continuation for this request
		final Continuation continuation = ContinuationSupport.getContinuation(request);

		// Handling the response
		HttpExchange exchange = new HttpExchange()
		{
			@Override
			public void onResponseComplete() throws IOException
			{
				jetRequest.setHandled(true);
				continuation.complete();
			}

			@Override
			public void onResponseContent(Buffer buffer) throws IOException
			{
				buffer.writeTo(out);
			}

			@Override
			public void onResponseStatus(Buffer version, int status, Buffer reason) throws IOException
			{
				response.setStatus(status);
			}

			@Override
			public void onResponseHeader(Buffer name, Buffer value) throws IOException
			{
				if (isFilteredHeader(name.toString()))
					return;

				if (name.equalsIgnoreCase(HttpHeaders.CONNECTION_BUFFER)
						&& value.equals(HttpHeaderValues.CLOSE_BUFFER))
					return;

				response.addHeader(name.toString(), value.toString());
			}

			@Override
			public void onConnectionFailed(Throwable ex)
			{
				onException(ex);
			}

			@Override
			public void onException(Throwable ex)
			{
				if (ex instanceof EOFException)
					return;

				if (response.isCommitted() == false)
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

				fireException(ex, obj);

				logger.error("exception", ex);
				jetRequest.setHandled(true);
				continuation.complete();
			}

			@Override
			public void onExpire()
			{
				if (response.isCommitted() == false)
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

				jetRequest.setHandled(true);
				continuation.complete();
			}
		};

		// Set exchange attributes
		if (request.getScheme().equals(HttpSchemes.HTTP))
			exchange.setScheme(HttpSchemes.HTTP_BUFFER);
		else
			exchange.setScheme(HttpSchemes.HTTPS_BUFFER);

		exchange.setMethod(request.getMethod());
		exchange.setURL(url.toString());
		exchange.setVersion(request.getProtocol());

		// Check the connection Header
		String connectionHeader = request.getHeader(HttpHeaders.CONNECTION);
		if (connectionHeader != null)
		{
			connectionHeader = connectionHeader.toLowerCase();
			if (connectionHeader.indexOf("keep-alive") < 0 && connectionHeader.indexOf("close") < 0)
				connectionHeader = null;
		}

		// Copy headers
		boolean hasContent = false;
		boolean xForwardedFor = true;
		long contentLength = -1;
		for (Enumeration<?> headerNames = request.getHeaderNames(); headerNames.hasMoreElements();)
		{
			String name = (String) headerNames.nextElement();
			name = name.toLowerCase();

			if (isFilteredHeader(name))
				continue;

			if (connectionHeader != null && connectionHeader.indexOf(name) >= 0)
				continue;

			if (name.equalsIgnoreCase(HttpHeaders.CONTENT_TYPE))
				hasContent = true;
			else if (name.equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH))
			{
				contentLength = request.getContentLength();
				exchange.setRequestHeader(HttpHeaders.CONTENT_LENGTH, TypeUtil.toString(contentLength));

				hasContent = contentLength > 0;
			} else if (name.equalsIgnoreCase(HttpHeaders.X_FORWARDED_FOR))
				xForwardedFor = true;

			for (Enumeration<?> values = request.getHeaders(name); values.hasMoreElements();)
			{
				String value = (String) values.nextElement();
				if (value == null)
					continue;

				exchange.setRequestHeader(name, value);
			}
		}

		// Add additional proxy headers
		exchange.setRequestHeader(HttpHeaders.VIA, "0.0 (prot)");
		if (xForwardedFor == false)
			exchange.addRequestHeader(HttpHeaders.X_FORWARDED_FOR, request.getRemoteAddr());

		// Set content body
		if (hasContent)
			exchange.setRequestContentSource(in);

		// Use a continuation to free this thread
		continuation.suspend();

		// Send the request
		httpClient.send(exchange);
	}

	public void setHttpClient(HttpClient httpClient)
	{
		this.httpClient = httpClient;
	}
}
