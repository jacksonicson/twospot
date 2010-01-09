package org.prot.controller.stats;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.prot.controller.app.AppInfo;
import org.prot.controller.app.AppRegistry;
import org.prot.util.scheduler.Scheduler;
import org.prot.util.scheduler.SchedulerTask;
import org.prot.util.stats.StatsValue;

public class Stats
{
	private static final Logger logger = Logger.getLogger(Stats.class);

	private AppRegistry registry;

	private final ControllerStats controllerStats = new ControllerStats();

	private List<BalancingProcessor> processors;

	public void init()
	{
		Scheduler.addTask(new StatsTask());
	}

	public void handle(final String appId)
	{
		try
		{
			controllerStats.handle();

			AppRequestStats stats = registry.getAppInfo(appId).getAppManagement().getAppRequestStats();
			stats.handle();
		} catch (Exception e)
		{
			logger.error("Error while handling stas: " + e);
		}
	}

	public void balance()
	{
		for (BalancingProcessor processor : processors)
			processor.run(registry.getDuplicatedAppInfos());
	}

	public void setRegistry(AppRegistry registry)
	{
		this.registry = registry;
	}

	public void setProcessors(List<BalancingProcessor> processors)
	{
		this.processors = processors;
	}

	public Map<String, Set<StatsValue>> getAppStats()
	{
		Map<String, Set<StatsValue>> stats = new HashMap<String, Set<StatsValue>>();
		for (AppInfo appInfo : registry.getDuplicatedAppInfos())
			stats.put(appInfo.getAppId(), appInfo.getAppManagement().getData());

		return stats;
	}

	public ControllerStats getControllerStats()
	{
		return controllerStats;
	}

	class StatsTask extends SchedulerTask
	{
		@Override
		public long getInterval()
		{
			return 5000;
		}

		@Override
		public void run()
		{
			try
			{
				balance();
			} catch (Exception e)
			{
				logger.error("StatsTask failed", e);
				System.exit(1);
			}
		}
	}
}
