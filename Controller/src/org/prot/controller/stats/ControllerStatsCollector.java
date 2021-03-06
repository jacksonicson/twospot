/*******************************************************************************
 * Copyright (c) 2010 Andreas Wolke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Wolke - initial API and implementation and initial documentation
 *******************************************************************************/
package org.prot.controller.stats;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.prot.controller.app.AppInfo;
import org.prot.controller.app.AppRegistry;
import org.prot.controller.config.Configuration;
import org.prot.controller.stats.processors.BalancingProcessor;
import org.prot.util.ISystemStats;
import org.prot.util.NativeSystemStats;
import org.prot.util.managment.gen.ManagementData;
import org.prot.util.scheduler.Scheduler;
import org.prot.util.scheduler.SchedulerTask;

public class ControllerStatsCollector {
	private static final Logger logger = Logger.getLogger(ControllerStatsCollector.class);

	private AppRegistry registry;

	private final ISystemStats systemStats = new NativeSystemStats();

	private long lastPoll = 0;

	private long requests = 0;

	private List<BalancingProcessor> processors;

	public void init() {
		Scheduler.addTask(new StatsTask());
	}

	public void handle(final AppInfo appInfo) {
		requests++;
	}

	public void balance() {
		for (BalancingProcessor processor : processors)
			processor.run(registry.getDuplicatedAppInfos());
	}

	void update(ManagementData.AppServer appServer) {
		AppInfo appInfo = registry.getAppInfo(appServer.getAppId());
		if (appInfo != null)
			appInfo.getAppManagement().update(appServer);
	}

	private float getRps() {
		double time = System.currentTimeMillis() - lastPoll;
		double rps = (double) requests / (time / 1000d + 1d);
		return (float) rps;
	}

	void fill(ManagementData.Controller.Builder controller) {
		controller.setAddress(Configuration.getConfiguration().getAddress());
		controller.setCpu(systemStats.getSystemLoad());
		controller.setProcCpu(systemStats.getProcessLoadSinceLastCall());
		controller.setIdleCpu(systemStats.getSystemIdle());
		controller.setTotalMem(systemStats.getTotalPhysicalMemorySize());
		controller.setFreeMem(systemStats.getFreePhysicalMemorySize());
		controller.setRps(getRps());

		Set<AppInfo> appInfos = registry.getDuplicatedAppInfos();
		controller.setRunningApps(appInfos.size());
		for (AppInfo info : appInfos) {
			if (info.getAppManagement().getAppServer() != null)
				controller.addAppServers(info.getAppManagement().getAppServer());
		}

		lastPoll = System.currentTimeMillis();
		requests = 0;
	}

	class StatsTask extends SchedulerTask {
		@Override
		public long getInterval() {
			return 5000;
		}

		@Override
		public void run() {
			try {
				balance();
			} catch (Exception e) {
				logger.error("StatsTask failed", e);
				System.exit(1);
			}
		}
	}

	public void setRegistry(AppRegistry registry) {
		this.registry = registry;
	}

	public void setProcessors(List<BalancingProcessor> processors) {
		this.processors = processors;
	}
}
