package org.prot.controller.stats;

import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.prot.controller.app.AppInfo;
import org.prot.controller.app.AppState;

public class HighLoadProcessor implements BalancingProcessor
{
	private static final Logger logger = Logger.getLogger(HighLoadProcessor.class);

	private Connector connector;

	@Override
	public void run(Set<AppInfo> appInfos)
	{
		if (!connector.isStarted())
			return;

		if (connector.isLowResources())
		{
			logger.debug("Controller is under high load");

			AppInfo bestApp = null;
			double bestRps = Double.MAX_VALUE;

			AppInfo worstApp = null;
			double worstRps = Double.MIN_NORMAL;

			for (AppInfo appInfo : appInfos)
			{
				double rps = appInfo.getAppManagement().getAppRequestStats().getRps();
				if (bestRps > rps)
				{
					bestRps = rps;
					bestApp = appInfo;
				}

				if (worstRps < rps)
				{
					worstRps = rps;
					worstApp = appInfo;
				}
			}

			if (bestApp == null)
				return;

			if (worstApp == bestApp)
				return;

			logger.debug("Killing app because of high load");
			bestApp.setStatus(AppState.KILLED);
		}
	}

	public void setConnector(Connector connector)
	{
		this.connector = connector;
	}
}
