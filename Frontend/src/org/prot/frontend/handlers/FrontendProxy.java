package org.prot.frontend.handlers;

import java.io.IOException;
import java.net.ConnectException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.server.Request;
import org.prot.frontend.cache.AppCache;
import org.prot.frontend.cache.CacheResult;
import org.prot.manager.stats.ControllerInfo;
import org.prot.util.handler.HttpProxyHelper;

public class FrontendProxy extends HttpProxyHelper<RequestState>
{
	private static final Logger logger = Logger.getLogger(FrontendProxy.class);

	private AppCache appCache;

	public FrontendProxy()
	{
		super(false);
	}

	private final HttpURI buildUrl(Request baseRequest, HttpServletRequest request, ControllerInfo info,
			String appId)
	{
		// Build the destination url
		String address = info.getAddress();
		String uri = baseRequest.getUri().toString();
		StringBuilder builder = new StringBuilder(5 + 3 + address.length() + 1 + 4 + 1 + 10 + uri.length()
				+ 10);
		builder.append(request.getScheme());
		builder.append("://");
		builder.append(address);
		builder.append(":");
		builder.append(info.getPort());
		builder.append("/");
		builder.append(appId);
		builder.append(uri);

		// Create the URI
		return new HttpURI(builder.toString());
	}

	public void process(String appId, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{
		// Check if the cache holds a controller for this app
		CacheResult result = appCache.getController(appId);
		if (result.getControllerInfo() == null)
		{
			response.sendError(HttpStatus.INTERNAL_SERVER_ERROR_500,
					"Manager unreachable or did not return a Controller.");
			baseRequest.setHandled(true);
			return;
		}

		RequestState state = new RequestState(appId, baseRequest, request, response);
		state.setCached(result);

		HttpURI uri = buildUrl(baseRequest, request, result.getControllerInfo(), appId);
		this.forwardRequest(baseRequest, request, response, uri, state);
	}

	protected boolean handleStatus(Buffer version, int status, Buffer reason) throws IOException
	{
		if (status == HttpStatus.MOVED_TEMPORARILY_302)
		{
			logger.info("Moved 302");
			return false;
		}

		return false;
	}

	protected void requestFinished(RequestState state)
	{
		appCache.release(state.getCached());
	}

	protected void expired(RequestState state)
	{
		appCache.release(state.getCached());

		try
		{
			state.getResponse().sendError(HttpStatus.REQUEST_TIMEOUT_408,
					"Connection with the Controller timed out");
		} catch (IOException e)
		{
			logger.trace("IOException", e);
		}
	}

	protected boolean error(RequestState state, Throwable e)
	{
		appCache.release(state.getCached());

		// Frontend could not connect with the Controller
		if (e instanceof ConnectException)
		{
			try
			{
				state.getResponse().sendError(HttpStatus.INTERNAL_SERVER_ERROR_500,
						"Frontend could not communicate with the Controller");
				return true;
			} catch (IOException e1)
			{
				return false;
			}
		}

		return false;
	}

	public void setAppCache(AppCache appCache)
	{
		this.appCache = appCache;
	}
}
