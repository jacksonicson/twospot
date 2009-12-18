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
		Set<ControllerInfo> result = new HashSet<ControllerInfo>();

		boolean overloaded = false;

		double bestRanking = Double.MAX_VALUE;
		ControllerStats bestController = null;
		
		Map<String, ControllerStats> controllers = stats.getControllers();
		for (ControllerStats controller : controllers.values())
		{
			// Check if controller is running the application
			InstanceStats instance = controller.getInstance(appId);
			if (instance == null)
				continue;

			// This controller is running the application
			registry.getController(controller.getAddress());

			// Check if controller is overloaded
			overloaded |= instance.getValues().overloaded;

			// Rate this controller
			
		}

		if (!result.isEmpty() && !overloaded)
			return result;

		result.add(registry.getController(bestController.getAddress()));

		return result;
	}

	public void setStats(Stats stats)
	{
		this.stats = stats;
	}

}
