/*******************************************************************************
 * Copyright (c) 2010 Andreas Wolke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Wolke - initial API and implementation and initial documentation
 *******************************************************************************/
package org.prot.controller.stats.processors;

import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.prot.controller.app.AppInfo;
import org.prot.controller.app.AppLife;
import org.prot.controller.app.AppState;

public class HighLoadProcessor implements BalancingProcessor
{
	private static final Logger logger = Logger.getLogger(HighLoadProcessor.class);

	private Connector connector;

	@Override
	public void run(Set<AppInfo> appInfos)
	{
		// Check if the connector of this Controller is started
		if (!connector.isStarted())
			return;

		// Only if there are more than one application
		if (appInfos.size() > 1)
			return;

		// Check if this Controller is under a high load
		if (connector.isLowResources())
		{
			AppInfo bestApp = null;
			double bestRps = Double.MAX_VALUE;

			AppInfo worstApp = null;
			double worstRps = Double.MIN_NORMAL;

			// Find the best AppServer to kill
			for (AppInfo appInfo : appInfos)
			{
				// Check the state of the AppServer
				if (appInfo.getStatus().getLife() == AppLife.SECOND)
					continue;

				// Check if there is management data for this AppServer
				if (appInfo.getAppManagement().getAppServer() == null)
					continue;

				// RPS of the AppServer
				double rps = appInfo.getAppManagement().getAppServer().getRps();

				// Best AppServer
				if (bestRps > rps)
				{
					bestRps = rps;
					bestApp = appInfo;
				}

				// Worst AppServer
				if (worstRps < rps)
				{
					worstRps = rps;
					worstApp = appInfo;
				}
			}

			// Check if best and worst applications where found
			if (bestApp == null || worstApp == null)
				return;

			// Check if best application equals worst application
			if (worstApp == bestApp)
				return;

			// Only works if there are enough controller machines available!
			logger.debug("Banning application: " + bestApp.getAppId());
			bestApp.setState(AppState.BANNED);
		}
	}

	public void setConnector(Connector connector)
	{
		this.connector = connector;
	}
}
