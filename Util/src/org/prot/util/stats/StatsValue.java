package org.prot.util.stats;

import java.io.Serializable;

public interface StatsValue extends Serializable
{
	public void update(StatsUpdater updater);
}
