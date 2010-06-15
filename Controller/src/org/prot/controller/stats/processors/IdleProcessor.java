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
import org.prot.controller.app.AppInfo;
import org.prot.controller.app.AppState;
import org.prot.controller.config.Configuration;

public class IdleProcessor implements BalancingProcessor
{
	private static final Logger logger = Logger.getLogger(IdleProcessor.class);

	private static int IDLE_THREASHOLD = Integer.MAX_VALUE;

	public IdleProcessor()
	{
		Configuration config = Configuration.getConfiguration();
		try
		{
			IDLE_THREASHOLD = Integer.parseInt(config.getProperty("balance.idleProcessor.idleTime"));
		} catch (NumberFormatException e)
		{
			logger.fatal("Configuration failed", e);
			System.exit(1);
		}
	}

	@Override
	public void run(Set<AppInfo> appInfos)
	{
		long currentTime = System.currentTimeMillis();

		// Iterate over all AppServers
		for (AppInfo appInfo : appInfos)
		{
			// Check if the AppServer is online
			if (appInfo.getStatus() != AppState.ONLINE)
				continue;

			// Check if there are management data for the AppServer
			if (appInfo.getAppManagement().getAppServer() == null)
				logger.debug("Missing management data for the appserver");
			// continue;

			// Check when the AppServr has been used the last time
			long time = currentTime - appInfo.getTouch();
			if (time > IDLE_THREASHOLD)
			{
				logger.debug("Killing IDLE AppServer: " + appInfo.getAppId());
				appInfo.setState(AppState.KILLED);
			}
		}
	}
}
