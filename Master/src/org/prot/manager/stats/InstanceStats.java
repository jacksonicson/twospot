package org.prot.manager.stats;

import java.util.Set;

import org.apache.log4j.Logger;
import org.prot.util.stats.AppStat;
import org.prot.util.stats.BooleanStat;
import org.prot.util.stats.DoubleStat;
import org.prot.util.stats.IntegerStat;
import org.prot.util.stats.LongStat;
import org.prot.util.stats.StatType;
import org.prot.util.stats.StatsUpdater;
import org.prot.util.stats.StatsValue;

public class InstanceStats implements StatsUpdater
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

	public void update(Set<StatsValue> update)
	{
		assigned = false;
		lastUpdate = System.currentTimeMillis();

		for (StatsValue value : update)
			value.update(this);
	}

	public boolean isOld()
	{
		boolean old = false;
		old |= System.currentTimeMillis() - lastUpdate > 60 * 1000;
		old |= assigned && (System.currentTimeMillis() - assignmentTimestamp > 60 * 1000);
		return old;
	}

	@Override
	public void update(StatType key, IntegerStat value)
	{
	}

	@Override
	public void update(StatType key, DoubleStat value)
	{
		switch (key)
		{
		case REQUESTS_PER_SECOND:
			stat.rps = value.get();
			break;
		}
	}

	@Override
	public void update(StatType key, BooleanStat value)
	{
		switch (key)
		{
		case OVERLOADED:
			if (System.currentTimeMillis() - stat.overloadedHold > 30000)
			{
				stat.overloaded = value.get();
				stat.overloadedHold = System.currentTimeMillis();
			}
			break;
		}
	}

	@Override
	public void update(StatType key, LongStat value)
	{
	}

	@Override
	public void update(StatType key, AppStat value)
	{
	}

	public void dump()
	{
		stat.dump();
	}
}