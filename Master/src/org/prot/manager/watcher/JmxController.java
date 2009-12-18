package org.prot.manager.watcher;

import org.prot.controller.management.IJmxResources;

public class JmxController
{
	private final String address;

	private IJmxResources resources;

	public JmxController(String address)
	{
		this.address = address;
	}

	IJmxResources getJmxResources()
	{
		if (resources == null)
		{
			resources = (IJmxResources) ExceptionSafeProxy.newInstance(getClass().getClassLoader(),
					IJmxResources.class, address, "bean:name=resources");
		}

		return resources;
	}

	void release()
	{
		resources = null;
	}
}
