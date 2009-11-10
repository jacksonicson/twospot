package org.prot.frontend.handlers;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.prot.frontend.cache.AppCache;
import org.prot.manager.config.ControllerInfo;
import org.prot.manager.services.FrontendService;
import org.prot.util.handler.HttpProxyHandler;

public class FlowHandler extends HttpProxyHandler
{
	private static final Logger logger = Logger.getLogger(FlowHandler.class);

	private FrontendService frontendService;

	private AppCache appCache;

	public FlowHandler()
	{
		setup();
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{

		// Extract the appid from the url
		String appId = null;

		URL url = new URL(request.getRequestURL().toString());
		String host = url.getHost();
		if (host.indexOf(".") != -1)
		{
			appId = host.substring(0, host.indexOf("."));
			logger.debug("appid = " + appId);
		}

		if (appId == null)
		{
			logger.debug("no application id found");
			response.sendError(404);
			baseRequest.setHandled(true);
			return;
		}

		try
		{
			// Check cache
			ControllerInfo info = appCache.getController(appId);
			if (info == null)
			{
				// Ask the manager and cache the results
				info = frontendService.chooseAppServer("");
				appCache.cacheController(appId, info);
			} else
			{
				logger.info("Using cached entry"); 
			}

			String fUrl = request.getRequestURL().toString();
			fUrl = "http://" + info.getAddress() + ":" + info.getPort();

			String fUri = "/" + appId + request.getRequestURI();
			String fHost = info.getAddress() + ":" + info.getPort();

			forwardRequest(baseRequest, request, response, fUrl, fUri, fHost);

			baseRequest.setHandled(true);

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void setFrontendService(FrontendService frontendService)
	{
		this.frontendService = frontendService;
	}

	public void setAppCache(AppCache appCache)
	{
		this.appCache = appCache;
	}
}
