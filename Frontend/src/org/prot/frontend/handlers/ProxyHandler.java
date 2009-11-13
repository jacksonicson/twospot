package org.prot.frontend.handlers;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.prot.frontend.cache.AppCache;
import org.prot.manager.config.ControllerInfo;
import org.prot.manager.services.FrontendService;
import org.prot.util.handler.HttpProxyHelper;

public class ProxyHandler extends AbstractHandler
{
	private static final Logger logger = Logger.getLogger(ProxyHandler.class);

	protected FrontendService frontendService;

	private AppCache appCache;

	private HttpProxyHelper proxyHelper;

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{

		// Extract the appid from the url
		String appId = null;

		// Get the URL
		URL url = new URL(request.getRequestURL().toString());
		String host = url.getHost();
		if (host.indexOf(".") != -1)
		{
			appId = host.substring(0, host.indexOf("."));
		}

		if (appId == null)
		{
			logger.debug("no application id found");
			response.sendError(HttpStatus.NOT_FOUND_404);
			baseRequest.setHandled(true);
			return;
		}

		try
		{
			// Check cache
			appCache.updateCache();
			ControllerInfo info = appCache.getController(appId);
			if (info == null)
			{
				// Ask the manager and cache the results
				info = frontendService.chooseAppServer(appId);
				appCache.cacheController(appId, info);
			}

			String fUrl = request.getRequestURL().toString();
			fUrl = "http://" + info.getAddress() + ":" + info.getPort();
			String fUri = "/" + appId + request.getRequestURI();
			fUrl = fUrl + fUri;

			logger.debug("Forarding to URL: " + fUrl);

			HttpURI uri = new HttpURI(fUrl);
			proxyHelper.forwardRequest(baseRequest, request, response, uri);

		} catch (Exception e)
		{
			logger.error("Erro while handling the request", e); 
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500); 
			baseRequest.setHandled(true);
			return;
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

	public void setProxyHelper(HttpProxyHelper proxyHelper)
	{
		this.proxyHelper = proxyHelper;
	}
}
