package org.prot.manager.stats;

import org.apache.log4j.Logger;
import org.prot.manager.config.Configuration;
import org.prot.util.managment.gen.ManagementData;

public class InstanceStats
{
	private static final Logger logger = Logger.getLogger(InstanceStats.class);

	private final String appId;

	private long lastUpdated;

	private long assignmentCounter = 0;

	public class StatValues
	{
		public double procCpu;
		public double rps;
		public boolean overloaded;
		public long overloadedHold;
		public long runtime;

		public long cpuTotal;
		private long[] cpuTotalHistory = null;

		public long cpuProcTotal;
		public long[] cpuProcTotalHistory = null;

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
			logger.debug("   RPS: " + rps);
			logger.debug("   Overloaded: " + overloaded);
			logger.debug("   Overloaded hold: " + overloadedHold);
			logger.debug("   Proc CPU: " + procCpu);
			logger.debug("   Runtime: " + runtime);
			logger.debug("   CPU units: " + getCpuUnits());
		}
	}

	final private StatValues stat = new StatValues();

	InstanceStats(String appId)
	{
		this.appId = appId;
	}

	InstanceStats(String appId, boolean assigned)
	{
		this(appId);
		this.assignmentCounter = 3;
	}

	public StatValues getValues()
	{
		return this.stat;
	}

	public String getAppId()
	{
		return appId;
	}

	void update(ManagementData.AppServer appServer)
	{
		this.assignmentCounter = 0;
		this.lastUpdated = System.currentTimeMillis();

		this.stat.procCpu = appServer.getProcCpu();
		this.stat.overloaded = appServer.getOverloaded();
		this.stat.rps = appServer.getRps();
		this.stat.runtime = appServer.getRuntime();

		this.stat.addCpuTotal(appServer.getCpuTotal());
		this.stat.addCpuProcTotal(appServer.getCpuProcTotal());
	}

	public boolean decrementAssignmentCounter()
	{
		boolean assigned = assignmentCounter > 0;
		if (assignmentCounter > 0)
			assignmentCounter--;

		return assigned;
	}

	public void dump()
	{
		stat.dump();
	}
}