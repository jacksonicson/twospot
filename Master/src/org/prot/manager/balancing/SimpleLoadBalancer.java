package org.prot.manager.balancing;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.prot.manager.data.ControllerInfo;
import org.prot.manager.data.ControllerRegistry;

public class SimpleLoadBalancer implements LoadBalancer
{
	private static final Logger logger = Logger.getLogger(SimpleLoadBalancer.class);

	private ControllerRegistry registry;

	@Override
	public Set<ControllerInfo> selectController(String appId)
	{
		Set<ControllerInfo> result = new HashSet<ControllerInfo>();

		Collection<ControllerInfo> infos = registry.getControllers();
		for (ControllerInfo info : infos)
		{
			Set<String> runningApps = info.getManagementData().getRunningApps();
			if (runningApps == null)
				continue;

			if (runningApps.contains(appId))
			{
				result.add(info);
				return result;
			}
		}

		if (infos.isEmpty() == false)
			result.add(infos.iterator().next());
		else
			logger.warn("Master doesn't have a Controller");

		return result;
	}

	public void setRegistry(ControllerRegistry registry)
	{
		this.registry = registry;
	}
}
