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
import org.prot.controller.security.RequestManager;
import org.prot.util.AppIdExtractor;

public class RequestHandler extends AbstractHandler
{
	private static final Logger logger = Logger.getLogger(RequestHandler.class);

	private static final String APP_SERVER_HOST = "localhost";

	private AppManager appManager;

	private RequestManager requestManager;

	private ControllerProxy controllerProxy;

	public void init()
	{
		// Do nothing
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
		// this request when the AppServer is online. If a continuation is used
		// the appInfo is null - this method will return for now. If the
		// continuation continues this handle method will be called again and
		// the appInfo than is *not* null
		if (appInfo == null)
			return;

		// Check the State of the AppInfo
		switch (appInfo.getStatus())
		{
		case FAILED:
			response.sendError(HttpStatus.NOT_FOUND_404);
			response.sendError(HttpStatus.NOT_FOUND_404, "AppServer has failed");
			return;
		case STALE:
			response.sendError(HttpStatus.INTERNAL_SERVER_ERROR_500);
			response.sendError(HttpStatus.INTERNAL_SERVER_ERROR_500, "AppServer is stale");
			return;
		}

		// Forward the request
		HttpURI destination = getUrl(baseRequest, appInfo);

		// Register the request in the RequestManager.
		requestManager.registerRequest(appInfo.getAppId(), baseRequest, request, response, destination);
	}

	public void setAppManager(AppManager appManager)
	{
		this.appManager = appManager;
	}

	public void setControllerProxy(ControllerProxy controllerProxy)
	{
		this.controllerProxy = controllerProxy;
	}

	public void setRequestManager(RequestManager requestManager)
	{
		this.requestManager = requestManager;
	}
}
