package org.prot.controller.stats;

import java.util.Set;

import org.eclipse.jetty.server.Connector;
import org.prot.controller.app.AppInfo;

public class LowRequestsProcessor implements BalancingProcessor
{
	private Connector connector;

	@Override
	public void run(Set<AppInfo> appInfos)
	{
		// TODO
	}
}
