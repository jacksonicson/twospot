package org.prot.manager.balancing;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
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
		// Get infos about the available controllers
		Collection<ControllerInfo> infos = registry.getControllers();

		// Check if there are controllers available
		if (infos.isEmpty())
		{
			logger.warn("Master does not have any controllers");
			return new HashSet<ControllerInfo>();
		}

		Set<ControllerInfo> result = new HashSet<ControllerInfo>();

		// Check if a Controller is already running this app
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

		// Randomly select a new Controller
		int size = infos.size();
		ControllerInfo[] info = new ControllerInfo[size];
		info = infos.toArray(info);

		Random random = new Random();
		result.add(info[Math.abs(random.nextInt()) % info.length]);

		return result;
	}

	public void setRegistry(ControllerRegistry registry)
	{
		this.registry = registry;
	}
}
