package org.prot.manager.stats;

import org.apache.log4j.Logger;
import org.prot.util.managment.gen.ManagementData;

public class InstanceStats
{
	private static final Logger logger = Logger.getLogger(InstanceStats.class);

	private final String appId;

	private long lastUpdate;

	private long assignmentCounter = 0;

	public class StatValues
	{
		public double procCpu;
		public double rps;
		public boolean overloaded;
		public long overloadedHold;
		public float load;

		public void dump()
		{
			logger.debug("   RPS: " + rps);
			logger.debug("   Overloaded: " + overloaded);
			logger.debug("   Overloaded hold: " + overloadedHold);
			logger.debug("   Load: " + load);
			logger.debug("   Proc CPU: " + procCpu);
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
		this.lastUpdate = System.currentTimeMillis();

		this.stat.procCpu = appServer.getCpu();
		this.stat.overloaded = appServer.getOverloaded();
		this.stat.rps = appServer.getRps();
		this.stat.load = appServer.getLoad();
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