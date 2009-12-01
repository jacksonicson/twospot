package org.prot.manager.watcher;

import org.prot.controller.management.jmx.IJmxDeployment;
import org.prot.controller.management.jmx.IJmxResources;

public class JmxControllerConnection
{
	private final String address;

	private IJmxDeployment deploy;

	private IJmxResources resources;

	public JmxControllerConnection(String address)
	{
		this.address = address;
	}

	IJmxDeployment getJmxDeployment()
	{
		if (deploy == null)
		{
			deploy = (IJmxDeployment) ExceptionSafeProxy.newInstance(getClass().getClassLoader(),
					IJmxDeployment.class, address, "bean:name=deployment");
		}

		return deploy;
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
}
