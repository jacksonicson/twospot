package org.prot.appserver.management;

import java.util.Set;

import org.prot.util.stats.StatsValue;

public interface Management
{
	public Set<StatsValue> ping();
}
