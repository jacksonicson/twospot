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
import org.prot.controller.app.AppInfo;
import org.prot.controller.app.AppManager;
import org.prot.controller.stats.Stats;
import org.prot.util.AppIdExtractor;

public class RequestHandler extends AbstractHandler
{
	private static final Logger logger = Logger.getLogger(RequestHandler.class);

	private static final String APP_SERVER_HOST = "localhost";

	private AppManager appManager;

	private RequestProcessor requestProcessor;

	private Stats stats;

	private final HttpURI getUrl(final Request request, final AppInfo appInfo)
	{
		// Get the connection settings
		String scheme = request.getScheme();
		int port = appInfo.getPort();
		String uri = request.getUri().toString();

		// Build the complete URL (In this case string concation seems to be
		// inefficient)
		StringBuilder builder = new StringBuilder(scheme.length() + 4 + uri.length() + 10);
		builder.append(scheme);
		builder.append("://");
		builder.append(APP_SERVER_HOST);
		builder.append(":");
		builder.append(port);
		builder.append(uri.substring(1 + appInfo.getAppId().length()));

		return new HttpURI(builder.toString());
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
			response.sendError(HttpStatus.NOT_FOUND_404, "Invalid AppId (scheme://domain/AppId/...)");
			baseRequest.setHandled(true);
			return;
		}

		// Inform the AppManager
		AppInfo appInfo = appManager.requireApp(appId);

		// The AppServer is not avialable - a continuation is used to restart
		// this request when the AppServer is online. If a continuation is used
		// the appInfo is null - this method will return for now. If the
		// continuation continues this handle method will be called again and
		// the appInfo than is *not* null
		if (appInfo == null)
		{
			logger.debug("Wait until continuation resumes");
			return;
		}

		// Update stats
		stats.handle(appId);

		// Check the State of the AppInfo
		switch (appInfo.getStatus())
		{
		case FAILED:
			response.sendError(HttpStatus.NOT_FOUND_404, "Could not start AppServer");
			baseRequest.setHandled(true);
			return;
		case STALE:
			response.sendError(HttpStatus.INTERNAL_SERVER_ERROR_500,
					"Could not communicate with the AppServer");
			baseRequest.setHandled(true);
			return;
		}

		// Create a destination URL to forward the request
		HttpURI destination = getUrl(baseRequest, appInfo);

		// Register the request in the RequestManager.
		requestProcessor.process(appInfo.getAppId(), baseRequest, request, response, destination);
	}

	public void setAppManager(AppManager appManager)
	{
		this.appManager = appManager;
	}

	public void setRequestProcessor(RequestProcessor requestProcessor)
	{
		this.requestProcessor = requestProcessor;
	}

	public void setStats(Stats stats)
	{
		this.stats = stats;
	}
}
