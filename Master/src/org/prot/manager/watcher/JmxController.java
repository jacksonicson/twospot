package org.prot.manager.watcher;

import org.prot.controller.management.JmxPing;

public class JmxController
{
	private final String address;

	private JmxPing ping;

	public JmxController(String address)
	{
		this.address = address;
	}

	JmxPing getJmxResources()
	{
		if (ping == null)
		{
			ping = (JmxPing) ExceptionSafeProxy.newInstance(getClass().getClassLoader(), JmxPing.class,
					address, "bean:name=resources");
		}

		return ping;
	}

	void release()
	{
		ping = null;
	}
}
