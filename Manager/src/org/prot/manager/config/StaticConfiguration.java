package org.prot.manager.config;

import java.util.HashSet;
import java.util.Set;

public class StaticConfiguration
{
	private Set<ControllerInfo> controllers = new HashSet<ControllerInfo>();

	{
		ControllerInfo info;
		
		info = new ControllerInfo(); 
		info.setAddress("localhost");
		info.setPort(8080);
		info.setServiceName("ControllerService");
		info.setServicePort(2299);
		controllers.add(info); 
	}
	
	public Set<ControllerInfo> getControllers()
	{
		return controllers;
	}
}
