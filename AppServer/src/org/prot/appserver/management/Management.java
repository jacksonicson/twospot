package org.prot.appserver.management;

import java.lang.management.ManagementFactory;
import java.util.List;

import javax.management.MBeanServer;

public class Management
{
	private List<?> beans;

	public void init()
	{
		// Register all Beans in the management server
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();

		// try
		// {
		// MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		// ObjectName name = new ObjectName("org.prot:type=ServerStatus");
		// mbs.registerMBean(stat, name);
		//
		// } catch (Exception e)
		// {
		// e.printStackTrace();
		// }
	}

	void handleConnection()
	{
	}
}
