package org.prot.controller.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.server.Request;
import org.prot.controller.manager.AppInfo;
import org.prot.controller.manager.AppManager;
import org.prot.util.handler.HttpProxyHandler;

public class RequestHandler extends HttpProxyHandler
{
	private static final Logger logger = Logger.getLogger(RequestHandler.class);

	private AppManager appManager;

	public void setAppManager(AppManager appManager)
	{
		this.appManager = appManager;
	}

	@Override
	protected String getHost(Request request)
	{
		return "localhost";
	}

	@Override
	protected String getUri(Request request)
	{
		String uri = request.getUri().toString().substring(1);
		uri = uri.substring(uri.indexOf("/"));
		return uri;
	}

	protected String getUrl(Request request, AppInfo appInfo)
	{
		String scheme = request.getScheme();
		int port = appInfo.getPort();
		String url = scheme + "://" + "localhost" + ":" + port;
		return url;
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
			response.getOutputStream().print("Error: Missing AppId");
			response.getOutputStream().close();
			return;
		}
		String appId = uri.substring(0, index);

		// retries if forward fails
		for (int i = 0; i < 3; i++)
		{
			try
			{
				// inform the AppManager
				logger.info("require app");
				AppInfo appInfo = this.appManager.requireApp(appId);
				// the AppServer is not avialable jet - a continuation is used
				// to restart this request when the AppServer is online
				if (appInfo == null)
					return; // Continues when the AppServer is available

				// Generate the URL to the destination Server
				String url = getUrl(baseRequest, appInfo);
				String host = "localhost:" + appInfo.getPort();
				uri = getUri(baseRequest);

				// forward the request
				forwardRequest(baseRequest, request, response, url, uri, host);
				
//				baseRequest.setHandled(true);
//
//				baseRequest.getInputStream().close();
//				response.getOutputStream().close();

				break;

			} catch (Exception e)
			{
				logger.error(e.getMessage(), e);
			}
		}
	}
}
