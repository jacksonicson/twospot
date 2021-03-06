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
package org.prot.appserver.runtime.jython;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.HttpConnection;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.prot.appserver.app.AppInfo;
import org.prot.appserver.config.Configuration;
import org.python.core.PyCode;
import org.python.core.PyDictionary;
import org.python.util.PythonInterpreter;

public class PythonHandler extends AbstractHandler
{
	private static final Logger logger = Logger.getLogger(PythonHandler.class);

	private EnginePool enginePool;

	private CodeBuffer codeBuffer;

	private AppInfo appInfo;

	public void setAppInfo(AppInfo appInfo)
	{
		this.appInfo = appInfo;
		this.codeBuffer = new CodeBuffer(Configuration.getInstance().getAppDirectory());
		this.enginePool = new EnginePool();
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{
		try
		{
			logger.debug("start python handle");

			// Get the URI
			String uri = baseRequest.getUri().toString();

			// Get the python-file
			PythonConfiguration rtConfig = (PythonConfiguration) appInfo.getRuntimeConfiguration();
			Set<WebConfiguration> webConfigs = rtConfig.getWebConfigs();
			String script = null;
			for (WebConfiguration config : webConfigs)
			{
				if ((script = config.matches(uri)) != null)
					break;
			}

			// Error-Handling if no python-file found
			if (script == null)
			{
				logger.debug("Request does not match a python file");
				response.setStatus(HttpStatus.NOT_FOUND_404);
				baseRequest.setHandled(true);
				return;
			}

			// Create a new IO channel
			HttpConnection httpConnection = HttpConnection.getCurrentConnection();
			Request srcRequest = httpConnection.getRequest();
			Response srcResponse = httpConnection.getResponse();
			WsgiChannel io = new WsgiChannel(srcRequest, srcResponse);

			// Dictionary which contains the request header
			PyDictionary dict = new PyDictionary();
			for (Enumeration<String> names = baseRequest.getHeaderNames(); names.hasMoreElements();)
			{
				String name = names.nextElement();
				String value = baseRequest.getHeader(name);
				dict.put(name, value);
			}

			dict.put("REQUEST_METHOD", baseRequest.getMethod());
			dict.put("SERVER_NAME", baseRequest.getServerName());
			dict.put("SERVER_PORT", baseRequest.getServerPort());
			dict.put("PATH_INFO", baseRequest.getUri());

			// Acquire an engine
			PythonInterpreter engine = enginePool.acquireInterpreter();

			// Load the script
			PyCode code = codeBuffer.loadScript(engine, script);

			// If the script ist not available - NOT FOUND 404
			if (code == null)
			{
				logger.error("Could not load python script");
				response.setStatus(HttpStatus.NOT_FOUND_404);
				baseRequest.setHandled(true);
				return;
			}

			try
			{
				// Set engine environment
				engine.set("headers", dict);
				engine.set("wsgiChannel", io);
				engine.set("threadName", Thread.currentThread().getName());

				// Execute the script
				engine.exec(code);

			} catch (Exception e)
			{
				logger.error(e);
			}

			baseRequest.setHandled(true);

		} catch (Exception e)
		{
			logger.error("Error in the python handler", e);
			System.exit(1);
		}
	}
}
