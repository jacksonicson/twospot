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
package org.prot.httpfileserver.handlers;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpMethods;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

public class DownloadResourceHandler extends ResourceHandler {
	private static final Logger logger = Logger
			.getLogger(DownloadResourceHandler.class);

	public void setResourceBase(String resourceBase) {
		Resource res;
		try {
			res = Resource.newResource(resourceBase);
			super.setBaseResource(res);
			logger.info("Using resourceBase: " + resourceBase);

		} catch (MalformedURLException e) {
			logger.error("Could not set base resource", e);
		} catch (IOException e) {
			logger.error("Could not set base resource", e);
		}
	}

	protected Resource getResource(HttpServletRequest request)
			throws MalformedURLException {
		// Check Method
		if (request.getMethod().equals(HttpMethods.GET) == false) {
			logger.debug("Could not download file - use HTTP GET");
			return null;
		}

		// Get the request URI
		String uri = request.getRequestURI();

		// Kill the first slash
		if (uri.startsWith("/"))
			uri = uri.substring(1);

		String appId = null;
		String version = null;
		int index = uri.indexOf('/');

		// Check if there is a version information
		// URL: http://host:port/appId/version/*
		if (index >= 0) {
			appId = uri.substring(0, index);
			uri = uri.substring(index + 1);

			index = uri.indexOf('/');
			if (index >= 0)
				uri = uri.substring(0, index);

			if (uri == "")
				version = "null";
			else
				version = uri;

		} else {
			appId = uri;
			version = "null";
		}

		// Only use lower case appIds and versions
		appId = appId.toLowerCase();
		version = version.toLowerCase();

		// Debug
		logger.debug("Loading resource with AppId: " + appId + " Version: "
				+ version);

		// Load the WAR file
		String file = appId + version + ".war";
		return getResource("/" + file);
	}
}
