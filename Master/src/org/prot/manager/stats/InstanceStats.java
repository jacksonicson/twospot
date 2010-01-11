package org.prot.manager.stats;

import org.apache.log4j.Logger;
import org.prot.util.managment.gen.ManagementData;

public class InstanceStats
{
	private static final Logger logger = Logger.getLogger(InstanceStats.class);

	private final String appId;

	long lastUpdate;

	boolean assigned = false;
	long assignmentTimestamp;

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
		}
	}

	final private StatValues stat = new StatValues();

	public InstanceStats(String appId)
	{
		this.appId = appId;
	}

	public InstanceStats(String appId, boolean assigned)
	{
		this(appId);
		this.assigned = true;
	}

	public StatValues getValues()
	{
		return this.stat;
	}

	public String getAppId()
	{
		return appId;
	}

	public void update(ManagementData.AppServer appServer)
	{
		assigned = false;
		lastUpdate = System.currentTimeMillis();
	}

	public boolean isOld()
	{
		boolean old = false;
		old |= System.currentTimeMillis() - lastUpdate > 60 * 1000;
		old |= assigned && (System.currentTimeMillis() - assignmentTimestamp > 60 * 1000);
		return old;
	}

	public void dump()
	{
		stat.dump();
	}
}