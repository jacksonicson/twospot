package org.prot.controller.handler;

import java.io.IOException;
import java.net.ConnectException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.server.Request;
import org.prot.controller.manager.AppManager;

public class RequestManager
{
	private static final Logger logger = Logger.getLogger(RequestManager.class);

	private AppManager appManager;

	private ControllerProxy controllerProxy;

	public void registerRequest(String appId, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response, HttpURI dest)
	{
		try
		{
			controllerProxy.forwardRequest(baseRequest, request, response, dest, appId);

		} catch (Exception e)
		{
			// Inform the client
			logger.error("Unknown error while handling the request", e);

			try
			{
				response.sendError(HttpStatus.INTERNAL_SERVER_ERROR_500,
						"Controller could not handle the request");
			} catch (IOException e1)
			{
				logger.error("Error while sending error", e1);
			}

			baseRequest.setHandled(true);
		}
	}

	public boolean requestError(String appId, Throwable t)
	{
		if (t instanceof ConnectException)
		{
			logger.debug("Reporting stale AppServer: " + appId);
			appManager.reportStaleApp(appId);
			return true;
		}

		return false;
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
