package org.prot.manager.balancing;

import java.util.Set;

import org.prot.manager.data.ControllerInfo;
import org.prot.manager.data.ControllerRegistry;

public class SimpleLoadBalancer implements LoadBalancer
{
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
