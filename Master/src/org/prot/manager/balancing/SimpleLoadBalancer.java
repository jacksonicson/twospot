package org.prot.manager.balancing;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.prot.controller.management.appserver.PerformanceData;
import org.prot.manager.stats.ControllerInfo;
import org.prot.manager.stats.ControllerRegistry;
import org.prot.manager.stats.ControllerStats;

public class SimpleLoadBalancer implements LoadBalancer
{
	private static final Logger logger = Logger.getLogger(SimpleLoadBalancer.class);

	private ControllerRegistry registry;

	@Override
	public Set<ControllerInfo> selectController(String appId)
	{
		return null;
	}

	public void setRegistry(ControllerRegistry registry)
	{
		this.registry = registry;
	}
}
