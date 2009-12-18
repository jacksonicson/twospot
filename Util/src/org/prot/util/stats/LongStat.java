package org.prot.util.stats;

public class LongStat implements StatsValue
{
	private final StatType key;

	private final long value;

	public LongStat(StatType key, long value)
	{
		this.key = key;
		this.value = value;
	}

	public long get()
	{
		return this.value;
	}

	@Override
	public void update(StatsUpdater updater)
	{
		updater.update(key, this);
	}
}
