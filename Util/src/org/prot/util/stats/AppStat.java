package org.prot.util.stats;

import java.util.Set;

public class AppStat implements StatsValue
{
	private final StatType key;

	private final String appId;

	private final Set<StatsValue> value;

	public AppStat(StatType key, String appId, Set<StatsValue> value)
	{
		this.key = key;
		this.appId = appId;
		this.value = value;
	}

	public String get()
	{
		return appId;
	}

	public Set<StatsValue> composite()
	{
		return this.value;
	}

	@Override
	public void update(StatsUpdater updater)
	{
		updater.update(key, this);
	}
}
