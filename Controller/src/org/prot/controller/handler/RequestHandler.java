package org.prot.controller.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.prot.controller.manager.AppInfo;
import org.prot.controller.manager.AppManager;
import org.prot.util.handler.HttpProxyHelper;

public class RequestHandler extends AbstractHandler
{
	private static final Logger logger = Logger.getLogger(RequestHandler.class);

	private AppManager appManager;

	private HttpProxyHelper proxyHelper;

	private HttpURI getUrl(Request request, AppInfo appInfo)
	{
		String scheme = request.getScheme();
		int port = appInfo.getPort();
		String uri = request.getUri().toString();

		if (uri.startsWith("/"))
			uri = uri.substring(1);
		uri = uri.substring(appInfo.getAppId().length());

		String url = scheme + "://" + "localhost" + ":" + port + uri;
		logger.debug("Request URL: " + url);

		return new HttpURI(url);
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{
		// extract appId
		String uri = baseRequest.getUri().getPath().substring(1);
		int index = uri.indexOf("/");
		if (index < 0)
		{
			response.sendError(HttpStatus.NOT_FOUND_404, "Missing AppId");
			baseRequest.setHandled(true);
			return;
		}
		String appId = uri.substring(0, index);

		// retries if forward fails
		for (int i = 0; i < 3; i++)
		{
			try
			{
				// inform the AppManager
				AppInfo appInfo = this.appManager.requireApp(appId);
				// the AppServer is not avialable jet - a continuation is used
				// to restart this request when the AppServer is online
				if (appInfo == null)
					return; // Continues when the AppServer is available

				// forward the request
				HttpURI newurl = getUrl(baseRequest, appInfo);
				proxyHelper.forwardRequest(baseRequest, request, response, newurl);
				break;

			} catch (Exception e)
			{
				logger.error("Error while handling the request (tried: " + i + ")", e);
			}
		}
	}

	public void setAppManager(AppManager appManager)
	{
		this.appManager = appManager;
	}

	public void setProxyHelper(HttpProxyHelper proxyHelper)
	{
		this.proxyHelper = proxyHelper;
	}
}
