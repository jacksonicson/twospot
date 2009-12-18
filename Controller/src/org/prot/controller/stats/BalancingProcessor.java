package org.prot.controller.stats;

import java.util.Set;

import org.prot.controller.app.AppInfo;

public interface BalancingProcessor
{
	public void run(Set<AppInfo> appInfos);
}
