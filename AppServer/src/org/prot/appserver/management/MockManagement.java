package org.prot.appserver.management;

import java.util.Set;

import org.prot.util.stats.StatsValue;

class MockManagement implements Management
{
	@Override
	public Set<StatsValue> ping()
	{
		return null;
	}
}
