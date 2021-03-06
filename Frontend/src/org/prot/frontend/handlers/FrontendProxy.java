/*******************************************************************************
 * Copyright (c) 2010 Andreas Wolke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Wolke - initial API and implementation and initial documentation
 *******************************************************************************/
package org.prot.frontend.handlers;

import java.io.IOException;
import java.net.ConnectException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.server.Request;
import org.prot.frontend.cache.AppCache;
import org.prot.frontend.cache.CacheResult;
import org.prot.manager.stats.ControllerInfo;
import org.prot.util.ErrorCodes;
import org.prot.util.handler.HttpProxyHelper;

public class FrontendProxy extends HttpProxyHelper<RequestState>
{
	private static final Logger logger = Logger.getLogger(FrontendProxy.class);

	private AppCache appCache;

	public FrontendProxy()
	{
		super(false);
	}

	private final HttpURI buildUrl(Request baseRequest, HttpServletRequest request, ControllerInfo info,
			String appId)
	{
		// Build the destination url
		String address = info.getAddress();
		String uri = baseRequest.getUri().toString();
		StringBuilder builder = new StringBuilder(5 + 3 + address.length() + 1 + 4 + 1 + 10 + uri.length()
				+ 10);

		builder.append(baseRequest.getScheme());
		builder.append("://");
		builder.append(address);
		builder.append(":");
		builder.append(info.getPort());
		builder.append("/");
		builder.append(appId);
		builder.append(uri);

		// Create the URI
		return new HttpURI(builder.toString());
	}

	public void process(String appId, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{
		// Check if the cache holds a controller for this app
		CacheResult result = appCache.getController(appId);
		if (result.getControllerInfo() == null)
		{
			response.sendError(ErrorCodes.CONTROLLER_BLOCKS,
					"Manager unreachable or did not return a Controller.");
			baseRequest.setHandled(true);
			return;
		}

		RequestState state = new RequestState(appId, baseRequest, request, response);
		state.setCached(result);

		HttpURI uri = buildUrl(baseRequest, request, result.getControllerInfo(), appId);
		this.forwardRequest(baseRequest, request, response, uri, state);
	}

	protected boolean handleStatus(RequestState state, Buffer version, int status, Buffer reason)
			throws IOException
	{
		if (status == ErrorCodes.CONTROLLER_BLOCKS)
		{
			logger.debug("Controller blocks: " + state.getAppId());
			logger.debug("Redirecting client to: " + state.getRequest().getRequestURL().toString());

			appCache.controllerBlocks(state.getAppId(), state.getCached().getControllerInfo().getAddress());
			state.getResponse().sendRedirect(state.getRequest().getRequestURL().toString());
			return true;
		}

		return false;
	}

	protected void requestFinished(RequestState state)
	{
		appCache.release(state.getCached());
	}

	protected void expired(RequestState state)
	{
		appCache.release(state.getCached());

		try
		{
			state.getResponse().sendError(HttpStatus.REQUEST_TIMEOUT_408,
					"Connection with the Controller timed out");
		} catch (IOException e)
		{
			logger.trace("IOException", e);
		}
	}

	protected boolean error(RequestState state, Throwable e)
	{
		appCache.release(state.getCached());

		// Frontend could not connect with the Controller
		if (e instanceof ConnectException)
		{
			try
			{
				state.getResponse().sendError(HttpStatus.INTERNAL_SERVER_ERROR_500,
						"Frontend could not communicate with the Controller");
				return true;
			} catch (IOException e1)
			{
				return true;
			}
		} else if (e instanceof IOException)
		{
			logger.trace("IOException", e);
			return true;
		}

		return false;
	}

	public void setAppCache(AppCache appCache)
	{
		this.appCache = appCache;
	}
}
