package org.prot.controller.handler;

import java.io.IOException;
import java.net.ConnectException;

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
import org.prot.util.handler.ExceptionListener;
import org.prot.util.handler.HttpProxyHelper;

public class RequestHandler extends AbstractHandler implements ExceptionListener
{
	private static final Logger logger = Logger.getLogger(RequestHandler.class);

	private AppManager appManager;

	private HttpProxyHelper proxyHelper;

	public void init()
	{
		proxyHelper.addExceptionListener(this);
	}

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
		// extract appId (if appId is missing return NOT FOUND 404)
		String uri = baseRequest.getUri().getPath().substring(1);
		int index = uri.indexOf("/");
		if (index < 0)
		{
			response.sendError(HttpStatus.NOT_FOUND_404, "Missing AppId");
			baseRequest.setHandled(true);
			return;
		}
		String appId = uri.substring(0, index);

		// Inform the AppManager
		AppInfo appInfo = null;
		appInfo = this.appManager.requireApp(appId);

		// the AppServer is not avialable jet - a continuation is used
		// to restart this request when the AppServer is online
		if (appInfo == null)
			return; // Continues when the AppServer is available

		try
		{
			// forward the request
			HttpURI newurl = getUrl(baseRequest, appInfo);
			proxyHelper.forwardRequest(baseRequest, request, response, newurl, appId);
			return;

		} catch (Exception e)
		{
			// Unknown exception
			logger.error("Unknown error while handling the request", e);
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

	@Override
	public void onException(Throwable e, Object obj)
	{
		logger.debug("Exception"); 
		String appId = (String) obj;
		if (e instanceof ConnectException)
		{
			logger.debug("Reporting stale AppServer " + appId);
			appManager.reportStaleApp(appId);
		}
	}
}
