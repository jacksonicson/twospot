package org.prot.util.stats;

public class DoubleStat implements StatsValue
{
	private final String key;

	private final double value;

	public DoubleStat(String key, double value)
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
