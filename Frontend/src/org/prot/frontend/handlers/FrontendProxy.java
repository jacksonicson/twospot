package org.prot.frontend.handlers;

import java.io.IOException;
import java.net.ConnectException;
import java.util.LinkedList;
import java.util.Queue;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.prot.frontend.cache.AppCache;
import org.prot.frontend.cache.CacheResult;
import org.prot.manager.stats.ControllerInfo;
import org.prot.util.handler.HttpProxyHelper;

public class FrontendProxy extends HttpProxyHelper<RequestState> implements Runnable
{
	private static final Logger logger = Logger.getLogger(FrontendProxy.class);

	private ThreadPool threadPool;

	private AppCache appCache;

	private Queue<RequestState> scheduled = new LinkedList<RequestState>();

	private boolean running = false;

	public FrontendProxy()
	{
		super(false);
	}

	private void startWorker()
	{
		// if (!running)
		// running = threadPool.dispatch(this);
	}

	private void schedule(RequestState state)
	{
		startWorker();

		synchronized (scheduled)
		{
			scheduled.add(state);
			scheduled.notifyAll();
		}
	}

	public void run()
	{
		while (true)
		{
			synchronized (scheduled)
			{
				try
				{
					while (scheduled.isEmpty())
					{
						scheduled.wait();
					}
				} catch (InterruptedException e)
				{
					continue;
				}

				RequestState state = scheduled.poll();
				try
				{
					reProcess(state);
				} catch (Exception e)
				{
					logger.trace(e);
				}
			}
		}
	}

	private void reProcess(RequestState state) throws Exception
	{
		if (state.isFull())
		{
			try
			{
				state.getResponse().sendError(HttpStatus.INTERNAL_SERVER_ERROR_500,
						"Communication with Controller's failed");
			} catch (IOException e)
			{
				// Do nothing
				logger.trace(e);
			} finally
			{
				state.getBaseRequest().setHandled(true);
			}
			return;
		}

		// TODO: Reprocess the request with another Controller
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
		state.useController(result.getControllerInfo().getAddress());
		state.setCached(result);

		HttpURI uri = buildUrl(baseRequest, request, result.getControllerInfo(), appId);
		this.forwardRequest(baseRequest, request, response, uri, state);
	}

	protected void requestFinished(RequestState state)
	{
		appCache.release(state.getCached());
	}

	@Override
	protected void expired(RequestState state)
	{
		try
		{
			state.getResponse().sendError(HttpStatus.REQUEST_TIMEOUT_408,
					"Connection with the Controller timed out");
		} catch (IOException e)
		{
			// Do nothing - happens if the client closed the connection
		}
	}

	protected boolean error(RequestState state, Throwable e)
	{
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
		} else
		{
			logger.warn("Proxy error", e);
		}

		return false;
	}

	public void setThreadPool(ThreadPool threadPool)
	{
		this.threadPool = threadPool;
	}

	public void setAppCache(AppCache appCache)
	{
		this.appCache = appCache;
	}
}
