package org.prot.manager.stats;

import org.apache.log4j.Logger;
import org.prot.util.managment.gen.ManagementData;

public class InstanceStats
{
	private static final Logger logger = Logger.getLogger(InstanceStats.class);

	private final String appId;

	private long lastUpdate;

	private Long assignmentTimestamp = null;

	public class StatValues
	{
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
		this.assignmentTimestamp = System.currentTimeMillis();
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
		assignmentTimestamp = null;
		lastUpdate = System.currentTimeMillis();

		this.stat.overloaded = appServer.getOverloaded();
		this.stat.rps = appServer.getRps();
		this.stat.load = appServer.getLoad();
	}

	public boolean isOld()
	{
		boolean old = false;
		old |= System.currentTimeMillis() - lastUpdate > 60 * 1000;
		old |= (assignmentTimestamp != null)
				&& (System.currentTimeMillis() - assignmentTimestamp > 60 * 1000);
		return old;
	}

	public void dump()
	{
		stat.dump();
	}
}