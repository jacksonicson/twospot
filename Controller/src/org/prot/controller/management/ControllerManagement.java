package org.prot.controller.management;

import java.beans.IntrospectionException;
import java.lang.management.ManagementFactory;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

public class ControllerManagement
{
	public ControllerManagement()
	{
		startJMX();
	}

	private void startJMX()
	{
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		System.out.println("MBean count: " + server.getMBeanCount());
		try
		{
			MBeanInfo info = server.getMBeanInfo(new ObjectName("java.lang:type=OperatingSystem"));
			System.out.println("CLassname: " + info.getClassName());

		} catch (InstanceNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedObjectNameException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ReflectionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (javax.management.IntrospectionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
