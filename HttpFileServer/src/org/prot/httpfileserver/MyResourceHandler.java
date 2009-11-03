package org.prot.httpfileserver;

import java.io.File;

import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.resource.Resource;

public class MyResourceHandler extends ResourceHandler
{

	public void setResourceBase(String resourceBase)
	{
		try
		{
			File file = new File(resourceBase);
			if(file.exists())
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
