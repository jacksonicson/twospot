package org.prot.util.stats;

public class IntegerStat implements StatsValue
{
	private final String key;

	private final double value;

	public IntegerStat(String key, double value)
	{
		this.key = key;
		this.value = value;
	}

	public double get()
	{
		return this.value;
	}

	@Override
	public void update(StatsUpdater updater)
	{
		updater.update(key, this);
	}
}
