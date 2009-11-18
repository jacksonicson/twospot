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
import org.prot.util.AppIdExtractor;
import org.prot.util.handler.ExceptionListener;
import org.prot.util.handler.HttpProxyHelper;

public class RequestHandler extends AbstractHandler implements ExceptionListener
{
	private static final Logger logger = Logger.getLogger(RequestHandler.class);

	private static final String APP_SERVER_HOST = "localhost"; 
	
	private AppManager appManager;

	private HttpProxyHelper proxyHelper;

	public void init()
	{
		proxyHelper.addExceptionListener(this);
	}

	private HttpURI getUrl(Request request, AppInfo appInfo)
	{
		// Get the connection settings
		String scheme = request.getScheme();
		int port = appInfo.getPort();
		String uri = request.getUri().toString();

		// Extract the application specific URI
		if (uri.startsWith("/"))
			uri = uri.substring(1);
		uri = uri.substring(appInfo.getAppId().length());

		// Build the complete URL
		String url = scheme + "://" + APP_SERVER_HOST + ":" + port + uri;

		logger.info("URL: " + url);
		
		return new HttpURI(url);
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{
		// Extract the AppId
		String appId = AppIdExtractor.fromUri(baseRequest.getUri().toString());

		// Check the AppId and send an error
		if (appId == null)
		{
			logger.debug("Invalid AppId"); 
			
			response.sendError(HttpStatus.NOT_FOUND_404, "Invalid AppId (scheme://domain/AppId/...)");
			baseRequest.setHandled(true);
			return;
		}

		// Inform the AppManager
		AppInfo appInfo = null;
		appInfo = this.appManager.requireApp(appId);

		// The AppServer is not avialable - a continuation is used to restart
		// this request when the AppServer is online
		if (appInfo == null)
			return;

		try
		{
			// Forward the request
			HttpURI destination = getUrl(baseRequest, appInfo);
			proxyHelper.forwardRequest(baseRequest, request, response, destination, appId);
			return;

		} catch (Exception e)
		{
			// Inform the client
			logger.error("Unknown error while handling the request", e);
			response.sendError(HttpStatus.INTERNAL_SERVER_ERROR_500,
					"Controller could not handle the request");
			baseRequest.setHandled(true);
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
	public boolean onException(Throwable e, Object obj)
	{
		if (e instanceof ConnectException)
		{
			String appId = (String) obj;
			logger.debug("Reporting stale AppServer for AppId: " + appId);
			
			appManager.reportStaleApp(appId);
			return true;
		}

		return false;
	}
}
