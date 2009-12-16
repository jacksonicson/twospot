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
		double[] ranking = new double[infos.length];
		int bestIndex = 0;
		double bestRanking = 0;
		for (int i = 0; i < infos.length; i++)
		{
			ControllerInfo info = infos[i];
			ManagementData management = info.getManagementData();

			// Smaller rankings are better!
			double rank = 0;
			rank += 1.0 * management.getAverageCpu();
			rank += 0.0 * management.getMemLoad(); // TODO: Range 0 to 1
			rank += 1.0 * management.getRunningApps().length; // TODO: Difficult
			// to decide ...
			// depens on mem
			// and cpu
			rank += 0.005 * management.getRps();
			ranking[i] = rank;

			logger.debug("Ranking for " + info.getAddress() + " = " + rank);

			if (bestRanking > ranking[i])
			{
				bestIndex = i;
				bestRanking = ranking[i];
			}
		}

		return infos[bestIndex];
	}

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

		// Results
		Set<ControllerInfo> result = new HashSet<ControllerInfo>();

		// Check if a Controller is already running this app
		// If a Controller is found - ask if the App requires more AppServers
		boolean requireMore = false;
		for (ControllerInfo info : infos)
		{
			PerformanceData data = info.getManagementData().getPerformanceData(appId);
			if (data != null)
			{
				logger.debug("Found Controller which is running the app");

				// Check if the AppServer is overloaded
				requireMore |= data.getLoad() > 0.6;

				// Add the AppServer to the list
				result.add(info);
			}
		}

		// Do we need to start another AppServer for this App?
		if (!requireMore && !result.isEmpty())
			return result;
		else
			logger.info("App requires more AppServers");

		// Find a good Controller and return the new Set
		result.add(findBestController(appId));
		return result;
	}

	public void setRegistry(ControllerRegistry registry)
	{
		this.registry = registry;
	}
}
