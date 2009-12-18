package org.prot.manager.balancing;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.prot.manager.stats.ControllerInfo;
import org.prot.manager.stats.ControllerRegistry;
import org.prot.manager.stats.ControllerStats;
import org.prot.manager.stats.InstanceStats;
import org.prot.manager.stats.Stats;

public class SimpleLoadBalancer implements LoadBalancer
{
	private static final Logger logger = Logger.getLogger(SimpleLoadBalancer.class);

	private ControllerRegistry registry;

	private Stats stats;

	@Override
	public Set<ControllerInfo> selectController(String appId)
	{
		if (stats.getControllers().isEmpty())
			return null;

		Set<ControllerInfo> result = new HashSet<ControllerInfo>();

		boolean overloaded = false;

		double bestRanking = Double.MAX_VALUE;
		ControllerStats bestController = null;

		Map<String, ControllerStats> controllers = stats.getControllers();
		for (ControllerStats controller : controllers.values())
		{
			// Rate this controller
			double rank = 0;
			rank += 0.3 * ((controller.getValues().cpu < 0) ? 0 : controller.getValues().cpu);
			rank += 2.0 * controller.getValues().freeMemory / controller.getValues().totalMemory;
			rank += 0.5 * controller.size();
			rank += 0.003 * controller.getValues().rps;

			logger.debug("Ranking: " + rank);

			// Check if its the new best controller
			if (rank < bestRanking || bestController == null)
			{
				bestRanking = rank;
				bestController = controller;
			}

			// Check if controller is running the application
			InstanceStats instance = controller.getInstance(appId);
			if (instance == null)
				continue;

			// This controller is running the application
			registry.getController(controller.getAddress());

			// Check if controller is overloaded
			overloaded |= instance.getValues().overloaded;
		}

		if (!result.isEmpty() && !overloaded)
			return result;

		ControllerInfo bestControllerInfo = registry.getController(bestController.getAddress());
		stats.assignToController(appId, bestController.getAddress());
		result.add(bestControllerInfo);

		return result;
	}

	public void setStats(Stats stats)
	{
		this.stats = stats;
	}

	public void setRegistry(ControllerRegistry registry)
	{
		this.registry = registry;
	}
}
