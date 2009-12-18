package org.prot.util.stats;

public interface StatsUpdater
{
	public void update(StatType key, IntegerStat value);

	public void update(StatType key, DoubleStat value);

	public void update(StatType key, BooleanStat value);

	public void update(StatType key, LongStat value);

	public void update(StatType key, AppStat value);
}
