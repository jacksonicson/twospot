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
package org.prot.manager.stats;

import org.apache.log4j.Logger;
import org.prot.manager.config.Configuration;
import org.prot.util.managment.gen.ManagementData;

public class InstanceStats
{
	private static final Logger logger = Logger.getLogger(InstanceStats.class);

	private final String appId;

	final private Values stat = new Values();

	private long assignmentTimer = 0;

	public class Values
	{
		public double procCpu;
		public double rps;
		private long overloaded;
		public long runtime;
		public float averageDelay;

		public long cpuTotal;
		private long[] cpuTotalHistory = null;

		public long cpuProcTotal;
		public long[] cpuProcTotalHistory = null;

		public void setOverloaded(boolean overloaded)
		{
			if (overloaded)
				this.overloaded = System.currentTimeMillis();
		}

		public boolean isOverloaded()
		{
			return (System.currentTimeMillis() - overloaded) < 20000;
		}

		public void addCpuTotal(long cpuTotal)
		{
			if (cpuTotalHistory == null)
			{
				cpuTotalHistory = new long[5];
				for (int i = 0; i < cpuTotalHistory.length; i++)
					cpuTotalHistory[i] = cpuTotal;

				this.cpuTotal = cpuTotal;
			}

			for (int i = 0; i < cpuTotalHistory.length - 1; i++)
				cpuTotalHistory[i] = cpuTotalHistory[i + 1];

			cpuTotalHistory[cpuTotalHistory.length - 1] = this.cpuTotal;
			this.cpuTotal = cpuTotal;
		}

		public void addCpuProcTotal(long cpuProcTotal)
		{
			if (cpuProcTotalHistory == null)
			{
				cpuProcTotalHistory = new long[5];
				for (int i = 0; i < cpuProcTotalHistory.length; i++)
					cpuProcTotalHistory[i] = cpuProcTotal;

				this.cpuProcTotal = cpuProcTotal;
			}

			for (int i = 0; i < cpuProcTotalHistory.length - 1; i++)
				cpuProcTotalHistory[i] = cpuProcTotalHistory[i + 1];

			cpuProcTotalHistory[cpuProcTotalHistory.length - 1] = this.cpuProcTotal;
			this.cpuProcTotal = cpuProcTotal;

		}

		public double getCpuUnits()
		{
			if (cpuTotalHistory != null)
			{
				long cpuTotalDiff = cpuTotalHistory[0] - cpuTotal;
				long cpuProcTotalDiff = cpuProcTotalHistory[0] - cpuProcTotal;

				if (cpuTotalDiff == 0 || cpuProcTotalDiff == 0)
					return 0;

				double unitLength = cpuTotalDiff / Configuration.getConfiguration().getSlbTotalCpuUnits();
				double usedUnits = cpuProcTotalDiff / unitLength;

				return usedUnits;
			}

			return 0;
		}

		public void dump()
		{
			logger.debug("      RPS:" + rps + " Overload:" + overloaded + " ProcCPU: " + procCpu
					+ " Runtime:" + runtime + " AverageDelay:" + averageDelay + " CPUUnits:" + getCpuUnits());
		}
	}

	InstanceStats(String appId)
	{
		this.appId = appId;
	}

	InstanceStats(String appId, boolean assigned)
	{
		this(appId);
		this.assignmentTimer = 5;
	}

	public Values getValues()
	{
		return this.stat;
	}

	public String getAppId()
	{
		return appId;
	}

	void update(ManagementData.AppServer appServer)
	{
		this.assignmentTimer = 0;

		this.stat.procCpu = appServer.getProcCpu();
		this.stat.setOverloaded(appServer.getOverloaded());
		this.stat.rps = appServer.getRps();
		this.stat.runtime = appServer.getRuntime();
		this.stat.averageDelay = appServer.getAverageDelay();

		this.stat.addCpuTotal(appServer.getCpuTotal());
		this.stat.addCpuProcTotal(appServer.getCpuProcTotal());
	}

	public boolean isAssigned()
	{
		return assignmentTimer > 0;
	}

	public boolean decrementAssignmentCounter()
	{
		boolean assigned = assignmentTimer > 0;
		if (assignmentTimer > 0)
			assignmentTimer--;

		return assigned;
	}

	public void dump()
	{
		stat.dump();
	}
}