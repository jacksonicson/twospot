package org.prot.manager.balancing;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.prot.manager.stats.ControllerInfo;
import org.prot.manager.stats.ControllerRegistry;
import org.prot.manager.stats.ControllerStats;
import org.prot.manager.stats.InstanceStats;

public class SimpleLoadBalancer implements LoadBalancer
{
	private static final Logger logger = Logger.getLogger(SimpleLoadBalancer.class);

	private ControllerRegistry registry;

	private ControllerInfo findBestController(String appId, Map<String, ControllerStats> controllers,
			Map<String, ControllerInfo> controllerInfos, Set<ControllerStats> selected)
	{
		// Used to find the best Controller (lowest load)
		double bestRanking = Double.MAX_VALUE;
		ControllerStats bestController = null;

		for (ControllerStats controller : controllers.values())
		{
			// Check if the management data of this controller are available
			if (!controllerInfos.containsKey(controller.getAddress()))
				continue;

			// Check if this controller is already in the selection
			if (selected.contains(controller))
				continue;

			// Calculate a ranking for the controller
			double rank = 0;
			rank += 0.3 * ((controller.getValues().cpu < 0) ? 0 : controller.getValues().cpu);
			rank += 2.0 * (controller.getValues().freeMemory / (controller.getValues().totalMemory + 1));
			rank += 0.5 * controller.size();
			rank += 0.003 * controller.getValues().rps;
			logger.debug("Ranking: " + rank);

			// Controller is not running the application
			// Check if its the new best controller
			if (rank < bestRanking || bestController == null)
			{
				bestRanking = rank;
				bestController = controller;
			}
		}

		if (bestController == null)
		{
			logger.debug("Could not find another Controller");
			return null;
		}

		ControllerInfo next = controllerInfos.get(bestController.getAddress());
		return next;
	}

	@Override
	public Set<ControllerInfo> selectController(String appId)
	{
		// Check if the master knows Controllers
		if (registry.isEmpty())
		{
			logger.warn("Master has no Controllers");
			return null;
		}

		// Fetch all Controllers (Threading-Problem)
		Map<String, ControllerInfo> controllerInfos = registry.getControllerInfos();
		Map<String, ControllerStats> controllerStats = registry.getControllers();

		// Set for the results
		Set<ControllerInfo> result = new HashSet<ControllerInfo>();
		Set<ControllerStats> resultStats = new HashSet<ControllerStats>();

		// Number of Controllers which report an overload
		int overloaded = 0;

		for (ControllerStats controller : controllerStats.values())
		{
			// Check if the controller is running the application
			InstanceStats instance = controller.getInstance(appId);
			if (instance == null)
				continue;

			// Add the controller to the result set
			ControllerInfo selected = controllerInfos.get(controller.getAddress());
			if (selected == null)
				continue;

			// The controller is running the requested application
			result.add(selected);
			resultStats.add(controller);

			// Check if controller reports an overload
			if (instance.getValues().overloaded || controller.getValues().overloaded)
				overloaded++;
		}

		// Check if we have found Controllers which are running the application
		if (!result.isEmpty())
		{
			int countControllers = result.size();
			double relativeOverload = (double) overloaded / (double) countControllers;

			// Return the current results if there are enough Controllers which
			// are not overloaded
			if (relativeOverload < 0.5d)
			{
				logger.debug("No overload " + relativeOverload + " - Returning # controllers: "
						+ result.size());
				return result;
			}
		}

		// The application requires another Controller - use the Controller with
		// the lowest load
		ControllerInfo bestControllerInfo = findBestController(appId, controllerStats, controllerInfos,
				resultStats);

		// Check if another Controller was found
		if (bestControllerInfo == null)
			return result;

		// Add the best controller
		result.add(bestControllerInfo);

		// Update internal stat data about this assignment (Controller -
		// Application)
		registry.assignToController(appId, bestControllerInfo.getAddress());

		// Return the results
		logger.debug("Returning # controllers: " + result.size());
		return result;
	}

	public void setRegistry(ControllerRegistry registry)
	{
		this.registry = registry;
	}
}
