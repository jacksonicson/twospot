package org.prot.util.stats;

public interface StatsUpdater
{
	public void update(String key, IntegerStat value);
	
	public void update(String key, DoubleStat value);
}
