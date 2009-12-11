package org.prot.httpfileserver.handlers;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpMethods;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

public class DownloadResourceHandler extends ResourceHandler
{
	private static final Logger logger = Logger.getLogger(DownloadResourceHandler.class);

	public void setResourceBase(String resourceBase)
	{
		Resource res;
		try
		{
			res = Resource.newResource(resourceBase);
			super.setBaseResource(res);
			logger.info("Using resourceBase: " + resourceBase);

		} catch (MalformedURLException e)
		{
			logger.error("Could not set base resource", e);
		} catch (IOException e)
		{
			logger.error("Could not set base resource", e);
		}
	}

	protected Resource getResource(HttpServletRequest request) throws MalformedURLException
	{
		// Check Method
		if (request.getMethod().equals(HttpMethods.GET) == false)
			return null;

		String uri = request.getRequestURI();

		// Kill the first slash
		if (uri.startsWith("/"))
			uri = uri.substring(1);

		String appId = null;
		String version = null;
		int index = uri.indexOf('/');

		// Check if there is a version information
		// URL: http://host:port/appId/version/*
		if (index >= 0)
		{
			appId = uri.substring(0, index);
			uri = uri.substring(index + 1);

			index = uri.indexOf('/');
			if (index >= 0)
				uri = uri.substring(0, index);

			if (uri == "")
				version = "nulL";
			else
				version = uri;

		} else
		{
			appId = uri;
			version = "null";
		}

		// Debug
		logger.debug("Resource with AppId: " + appId + " Version: " + version);

		// Create file
		String file = appId + version + ".war";
		return getResource("/" + file);
	}
}
