package org.prot.frontend.handlers;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.prot.frontend.ExceptionSafeFrontendProxy;
import org.prot.frontend.cache.AppCache;
import org.prot.manager.data.ControllerInfo;
import org.prot.manager.services.FrontendService;
import org.prot.util.AppIdExtractor;

public class ProxyHandler extends AbstractHandler
{
	private static final Logger logger = Logger.getLogger(ProxyHandler.class);

	private AppCache appCache;

	private FrontendProxy frontendProxy;

	private FrontendService frontendService;

	public void init()
	{
		frontendService = (FrontendService) ExceptionSafeFrontendProxy.newInstance(getClass()
				.getClassLoader(), FrontendService.class);
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{

		// Extract the appid from the url
		String appId = AppIdExtractor.fromDomain(request.getRequestURL().toString());

		if (appId == null)
		{
			// Error: Missing AppId
			response.sendError(HttpStatus.NOT_FOUND_404, "Missing AppId (scheme://AppId.domain...)");
			baseRequest.setHandled(true);
			return;
		}

		try
		{
			// Check if the cache holds a controller for this app
			appCache.updateCache();
			ControllerInfo info = appCache.getController(appId);

			// Cache missed
			if (info == null)
			{
				// Ask the manager and cache the controller
				Set<ControllerInfo> infoset = frontendService.selectController(appId); 
				if (infoset != null && infoset.size() > 0)
				{
					appCache.cacheController(appId, infoset.iterator().next());
				} else
				{
					response.sendError(HttpStatus.INTERNAL_SERVER_ERROR_500,
							"Manager unreachable or did not return a Controller");
					baseRequest.setHandled(true);
					return;
				}
			}

			// Build the destination url
			String url = info.getAddress() + ":" + info.getPort();
			String uri = "/" + appId + baseRequest.getUri().toString();
			url = url + uri;

			logger.debug("Forwarding request to: " + url);

			// Forward the request
			frontendProxy.forwardRequest(baseRequest, request, response, new HttpURI(url), response);

		} catch (Exception e)
		{
			logger.error("Error while processing the request", e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
			baseRequest.setHandled(true);
			return;
		}
	}

	public void setAppCache(AppCache appCache)
	{
		this.appCache = appCache;
	}

	public void setFrontendProxy(FrontendProxy frontendProxy)
	{
		this.frontendProxy = frontendProxy;
	}
}
