package org.prot.httpfileserver.handlers;

import java.io.File;
import java.net.MalformedURLException;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.resource.Resource;

public class DownloadResourceHandler extends ResourceHandler
{
	protected Resource getResource(HttpServletRequest request) throws MalformedURLException
	{
		String path_info = request.getPathInfo();
		String[] components = path_info.split("/");

		System.out.println("length: " + components.length);
		for (String s : components)
			System.out.println("S: " + s);

		if (components.length == 3 && components[1].equals("app"))
		{
			String appId = components[2];
			String file = "/" + appId + ".war";
			System.out.println("loading: " + file);
			return getResource(file);
		}

		return getResource("/failed.txt");
	}

	public void setResourceBase(String resourceBase)
	{
		try
		{
			File file = new File(resourceBase);
			if (file.exists())
				setBaseResource(Resource.newResource(file.getAbsolutePath()));
			else
				throw new IllegalArgumentException(resourceBase);
		} catch (Exception e)
		{
			Log.warn(e.toString());
			Log.debug(e);
			throw new IllegalArgumentException(resourceBase);
		}
	}
}
