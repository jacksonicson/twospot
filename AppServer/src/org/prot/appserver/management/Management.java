package org.prot.appserver.management;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

public class Management
{
	public void init()
	{
		try
		{
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			ObjectName name = new ObjectName("org.prot:type=ServerStatus"); 
			ServerStatus stat = new ServerStatus();
			mbs.registerMBean(stat, name);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
