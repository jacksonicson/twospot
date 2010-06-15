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
package org.prot.controller.handler;

import java.io.IOException;
import java.net.ConnectException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.server.Request;
import org.prot.controller.app.AppInfo;
import org.prot.controller.app.AppManager;
import org.prot.util.handler.HttpProxyHelper;

public class RequestProcessor extends HttpProxyHelper<AppInfo>
{
	private static final Logger logger = Logger.getLogger(RequestProcessor.class);

	private AppManager appManager;

	public RequestProcessor()
	{
		super(true);
	}

	public void process(AppInfo appInfo, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response, HttpURI dest)
	{
		try
		{
			forwardRequest(baseRequest, request, response, dest, appInfo);
		} catch (Exception e)
		{
			logger.error("RequestProcessor could not process the request", e);
			
			try
			{
				response.sendError(HttpStatus.INTERNAL_SERVER_ERROR_500, "ControllerProxy failed");
			} catch (IOException e1)
			{
				logger.trace(e1);
			}

			baseRequest.setHandled(true);
		}
	}

	protected void requestFinished(AppInfo appInfo)
	{
		appInfo.stopRequest();
	}
	
	@Override
	protected boolean error(AppInfo appInfo, Throwable t)
	{
		if (t instanceof ConnectException)
		{
			appManager.staleApp(appInfo);
			appInfo.stopRequest();
			return true;
		} else if (t instanceof IOException)
		{
			logger.trace("IOException in Controller", t);
			return true;
		}

		return false;
	}

	public void setAppManager(AppManager appManager)
	{
		this.appManager = appManager;
	}
}
