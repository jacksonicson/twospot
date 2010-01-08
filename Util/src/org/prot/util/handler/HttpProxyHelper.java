package org.prot.util.handler;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import org.eclipse.jetty.http.HttpHeaderValues;
import org.eclipse.jetty.http.HttpHeaders;
import org.eclipse.jetty.http.HttpSchemes;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.TypeUtil;

public class HttpProxyHelper<M>
{
	private static final Logger logger = Logger.getLogger(HttpProxyHelper.class);

	private HttpClient httpClient;

	private final Set<String> invalidHeaders = new HashSet<String>();

	private final boolean LIMIT_TRAFFIC;

	public HttpProxyHelper(boolean limitTraffic)
	{
		LIMIT_TRAFFIC = limitTraffic;
	}

	protected void setupInvalidHeaders()
	{
		invalidHeaders.add(HttpHeaders.PROXY_CONNECTION.toLowerCase());
		invalidHeaders.add(HttpHeaders.CONNECTION.toLowerCase());
		invalidHeaders.add(HttpHeaders.KEEP_ALIVE.toLowerCase());
		invalidHeaders.add(HttpHeaders.TRANSFER_ENCODING.toLowerCase());
		invalidHeaders.add(HttpHeaders.TE.toLowerCase());
		invalidHeaders.add(HttpHeaders.TRAILER.toLowerCase());
		invalidHeaders.add(HttpHeaders.PROXY_AUTHORIZATION.toLowerCase());
		invalidHeaders.add(HttpHeaders.PROXY_AUTHENTICATE.toLowerCase());
		invalidHeaders.add(HttpHeaders.UPGRADE.toLowerCase());
	}

	/**
	 * Called after the send method of the HttpClient has been called
	 * 
	 * @param management
	 *            the management object
	 * @param exchange
	 *            the HttpExchange object
	 */
	protected void requestSent(M management, HttpExchange exchange)
	{
		// This is a template method
	}

	/**
	 * The request from the HttpClient has expired
	 * 
	 * @param management
	 */
	protected void expired(M management)
	{
		// This is a template method
	}

	/**
	 * Finished processing the proxy request
	 * 
	 * @param management
	 */
	protected void requestFinished(M management)
	{
		// This is a template method
	}

	/**
	 * An error occured in the HttpClient
	 * 
	 * @param management
	 * @param t
	 * @return true if the method has handled the error. If false the error will
	 *         be logged.
	 */
	protected boolean error(M management, Throwable t)
	{
		// This is a template method
		return false;
	}

	private boolean isFilteredHeader(String header)
	{
		return invalidHeaders.contains(header);
	}

	public void forwardRequest(final Request jetRequest, final HttpServletRequest request,
			final HttpServletResponse response, final HttpURI url) throws Exception
	{
		forwardRequest(jetRequest, request, response, url, null);
	}

	class CountingInputStream extends InputStream
	{
		private long byteCounter = 0;

		private static final long limit = 1 * 1024 * 1024;

		private final InputStream target;

		public CountingInputStream(InputStream in)
		{
			this.target = in;
		}

		public long skip(long n) throws IOException
		{
			return target.skip(n);
		}

		public int available() throws IOException
		{
			return target.available();
		}

		public void close() throws IOException
		{
			target.close();
		}

		public synchronized void mark(int readlimit)
		{
			target.mark(readlimit);
		}

		public synchronized void reset() throws IOException
		{
			target.reset();
		}

		public boolean markSupported()
		{
			return target.markSupported();
		}

		public final int read(byte b[]) throws IOException
		{
			byteCounter += b.length;
			if (byteCounter > limit)
			{
				logger.warn("Limited input stream");
				throw new IOException("Limited input stream");
			}

			return target.read(b, 0, b.length);
		}

		public final int read(byte b[], int off, int len) throws IOException
		{
			byteCounter += len;
			if (byteCounter > limit)
			{
				logger.warn("Limited input stream");
				throw new IOException("Limited input stream");
			}

			return target.read(b, off, len);
		}

		@Override
		public final int read() throws IOException
		{
			byteCounter++;
			if (byteCounter > limit)
			{
				logger.warn("Limited input stream");
				throw new IOException("Limited input stream");
			}

			return target.read();
		}
	}

	public void forwardRequest(final Request jetRequest, final HttpServletRequest request,
			final HttpServletResponse response, final HttpURI url, final M obj) throws Exception
	{
		if (url == null)
			throw new NullPointerException("URL must not be null");

		// Check if its a CONNECT request
		if (request.getMethod().equalsIgnoreCase("CONNECT"))
		{
			response.sendError(HttpStatus.NOT_IMPLEMENTED_501);
			jetRequest.setHandled(true);
			return;
		}

		// Get the input and output streams
		final InputStream in;
		if (LIMIT_TRAFFIC)
			in = new CountingInputStream(request.getInputStream());
		else
			in = request.getInputStream();

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

				requestFinished(obj);
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

				boolean handled = error(obj, ex);

				if (!handled)
					logger.error("Unhandled exception in the ProxyHelper: ", ex);

				jetRequest.setHandled(true);
				continuation.complete();
			}

			@Override
			public void onExpire()
			{
				if (response.isCommitted() == false)
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

				expired(obj);

				jetRequest.setHandled(true);
				continuation.complete();
			}

			@Override
			public void cancel()
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
		exchange.setRequestHeader(HttpHeaders.VIA, "0.0 (twospot)");
		if (xForwardedFor == false)
			exchange.addRequestHeader(HttpHeaders.X_FORWARDED_FOR, request.getRemoteAddr());

		// Set content body
		if (hasContent)
			exchange.setRequestContentSource(in);

		// Use a continuation to free this thread
		continuation.suspend();

		// Send the request
		httpClient.send(exchange);
		requestSent(obj, exchange);
	}

	public void setHttpClient(HttpClient httpClient)
	{
		this.httpClient = httpClient;
	}
}
