package org.prot.controller.management;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;

import sun.nio.ch.SocketAdaptor;

public class ControllerManagement
{
	private MBeanServer mbeanServer; 
	
	public ControllerManagement()
	{
		
	}
	
	private void startJMX()
	{
		// Create the Server
		mbeanServer = MBeanServerFactory.createMBeanServer();
		
		
	}
}
