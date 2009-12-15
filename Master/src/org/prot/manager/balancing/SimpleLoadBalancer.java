package org.prot.manager.balancing;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;
import org.prot.controller.management.appserver.PerformanceData;
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
		ControllerInfo[] infos = registry.getControllers();

		// Check if there are controllers available
		if (infos.length == 0)
		{
			logger.warn("Master does not have any controllers");
			return null;
		}

		Set<ControllerInfo> result = new HashSet<ControllerInfo>();
		boolean requireMore = false;

		// Check if a Controller is already running this app
		for (ControllerInfo info : infos)
		{
			PerformanceData data = info.getManagementData().getPerformanceData(appId);
			if (data != null)
			{
				requireMore |= data.getLoad() > 0.6;
				result.add(info);
			}
		}

		if (requireMore)
		{
			logger.info("App requires more appservers");

			ControllerInfo best = null;
			double bestLoad = Double.MAX_VALUE;
			for (ControllerInfo info : infos)
			{
				if (result.contains(info))
					continue;

				double cpu = info.getManagementData().getAverageCpu();
				if (bestLoad > cpu)
				{
					bestLoad = cpu;
					best = info;
				}
			}

			// If we found another Controller
			if (best != null)
				result.add(best);

			return result;
		}

		// Randomly select a new Controller
		int size = infos.length;
		Random random = new Random();
		result.add(infos[Math.abs(random.nextInt()) % size]);

		return result;
	}

	public void setRegistry(ControllerRegistry registry)
	{
		this.registry = registry;
	}
}
