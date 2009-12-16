package org.prot.manager.balancing;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.prot.controller.management.appserver.PerformanceData;
import org.prot.manager.data.ControllerInfo;
import org.prot.manager.data.ControllerRegistry;
import org.prot.manager.data.ManagementData;

public class SimpleLoadBalancer implements LoadBalancer
{
	private static final Logger logger = Logger.getLogger(SimpleLoadBalancer.class);

	private ControllerRegistry registry;

	private ControllerInfo findBestController(String appId)
	{
		logger.debug("Finding best Controller");

		ControllerInfo[] infos = registry.getControllers();

		double bestRanking = Double.MAX_VALUE;
		ControllerInfo bestController = null;

		// Find the best Controller by a ranking algorithm
		for (int i = 0; i < infos.length; i++)
		{
			ControllerInfo info = infos[i];
			ManagementData management = info.getManagementData();

			// Smaller rankings are better!
			double rank = 0;
			rank += 1.0 * management.getAverageCpu();
			rank += 1.0 * management.getMemLoad();
			rank += 0.5 * management.getRunningApps().length;
			rank += 0.7 * info.assignedSize();
			rank += 0.005 * management.getRps();
			logger.debug("Ranking for " + info.getAddress() + " = " + rank);

			// Update best Controller
			if (bestRanking > rank)
			{
				bestRanking = rank;
				bestController = info;
			}
		}

		// Assign AppId to Controller
		bestController.assign(appId);

		logger.debug("Selected controller: " + bestController.getAddress());

		return bestController;
	}

	@Override
	public Set<ControllerInfo> selectController(String appId)
	{
		// Get infos about the available controllers
		ControllerInfo[] infos = registry.getControllers();

		// Check if there are controllers available
		if (infos.length == 0)
		{
			logger.warn("Master does not know any controllers");
			return null;
		}

		// Results
		Set<ControllerInfo> result = new HashSet<ControllerInfo>();

		// Check if a Controller is already running this app
		// If a Controller is found - ask if the App requires more AppServers
		boolean requireMore = false;
		for (ControllerInfo info : infos)
		{
			if (info.getManagementData().isRunning(appId))
			{
				PerformanceData data = info.getManagementData().getPerformanceData(appId);
				// Check if the AppServer is overloaded
				if (data != null)
					requireMore |= data.getLoad() > 0.6;

				// Add the AppServer to the list
				result.add(info);

			} else if (info.isAssigned(appId))
			{
				logger.debug("Assigned controller: " + info.getAddress());
				result.add(info);
			}
		}

		// Do we need to start another AppServer for this App?
		if (!requireMore && !result.isEmpty())
			return result;
		else
			logger.info("App requires a new AppServer. Is overloaded?: " + requireMore);

		// Find a good Controller and return the new Set
		result.add(findBestController(appId));

		return result;
	}

	public void setRegistry(ControllerRegistry registry)
	{
		this.registry = registry;
	}
}
