package org.prot.controller.security;

import java.io.IOException;
import java.net.ConnectException;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.server.Request;
import org.prot.controller.handler.ControllerProxy;
import org.prot.controller.manager.AppManager;

public class RequestManager
{
	private static final Logger logger = Logger.getLogger(RequestManager.class);

	private static final long MAX_REQUEST_RUNTIME = 45000;

	private AppManager appManager;

	private ControllerProxy controllerProxy;

	private static long requestCounter = 0;

	private List<RequestInfo> running = new Vector<RequestInfo>();

	private final long newRequestId()
	{
		return requestCounter++;
	}

	public RequestInfo registerRequest(String appId, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response, HttpURI dest)
	{
		RequestInfo info = new RequestInfo(newRequestId());
		info.setAppId(appId);
		info.setTimestamp(System.currentTimeMillis());
		info.setBaseRequest(baseRequest);
		info.setRequest(request);
		info.setResponse(response);
		info.setDestination(dest);

		// Start the request
		start(info);
		running.add(info);

		return info;
	}

	private void start(RequestInfo info)
	{
		// private HashMap<String, Set<RequestInfo>> map = new HashMap<String,
		// Set<RequestInfo>>();
		// for (RequestInfo test : running)
		// {
		// Set<RequestInfo> infos = map.get(test.getAppId());
		// if (infos == null)
		// infos = new HashSet<RequestInfo>();
		//
		// }
	}

	private void startReal(RequestInfo info)
	{
		try
		{
			controllerProxy.forwardRequest(info.getBaseRequest(), info.getRequest(), info.getResponse(), info
					.getDestination(), info);

			running.add(info);

		} catch (Exception e)
		{
			// Inform the client
			logger.error("Unknown error while handling the request", e);
			try
			{
				info.getResponse().sendError(HttpStatus.INTERNAL_SERVER_ERROR_500,
						"Controller could not handle the request");
			} catch (IOException e1)
			{
				logger.error("Error while sending error", e1);
			}

			info.getBaseRequest().setHandled(true);
		}
	}

	public void requestFinished(RequestInfo info)
	{
		logger.debug("Removing request: " + info.getRequestId());
		running.remove(info);
	}

	public boolean requestError(RequestInfo info, Throwable t)
	{
		requestFinished(info);

		if (t instanceof ConnectException)
		{
			String appId = info.getAppId();
			logger.debug("Reporting stale AppServer for AppId: " + appId);

			appManager.reportStaleApp(appId);
			return true;
		}

		return false;
	}

	public RequestManager()
	{
	}

	public void setAppManager(AppManager appManager)
	{
		this.appManager = appManager;
	}

	public void setControllerProxy(ControllerProxy controllerProxy)
	{
		this.controllerProxy = controllerProxy;
	}
}
