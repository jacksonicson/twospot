package org.prot.util.stats;

public class BooleanStat implements StatsValue
{
	private final StatType key;

	private final boolean value;

	public BooleanStat(StatType key, boolean value)
	{
		this.key = key;
		this.value = value;
	}

	public boolean get()
	{
		return this.value;
	}

	@Override
	public void update(StatsUpdater updater)
	{
		updater.update(key, this);
	}
}
