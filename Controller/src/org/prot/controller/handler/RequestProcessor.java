package org.prot.controller.handler;

import java.io.IOException;
import java.net.ConnectException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.server.Request;
import org.prot.controller.app.AppInfo;
import org.prot.controller.app.AppManager;
import org.prot.util.handler.HttpProxyHelper;

public class RequestProcessor extends HttpProxyHelper<AppInfo>
{
	private static final Logger logger = Logger.getLogger(RequestProcessor.class);

	private AppManager appManager;

	public RequestProcessor()
	{
		super(true);
	}

	public void process(AppInfo appInfo, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response, HttpURI dest)
	{
		try
		{
			forwardRequest(baseRequest, request, response, dest, appInfo);
		} catch (Exception e)
		{
			logger.error("Exception in Proxy", e);

			try
			{
				response.sendError(HttpStatus.INTERNAL_SERVER_ERROR_500, "ControllerProxy failed");
			} catch (IOException e1)
			{
				logger.trace(e1);
			}

			baseRequest.setHandled(true);
		}
	}

	@Override
	protected boolean error(AppInfo appInfo, Throwable t)
	{
		if (t instanceof ConnectException)
		{
			appManager.staleApp(appInfo);
			return true;
		} else if (t instanceof IOException)
		{
			logger.trace("IOException in Controller");
			return true;
		}

		return false;
	}

	public void setAppManager(AppManager appManager)
	{
		this.appManager = appManager;
	}
}
