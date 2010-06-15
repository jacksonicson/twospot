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
package org.prot.app.security;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.prot.appserver.config.Configuration;

public class DosPrevention
{
	private static final Logger logger = Logger.getLogger(DosPrevention.class);

	// Is read from the Configuration when the object is created
	private final long MAX_REQUEST_TIME;

	// Request counter
	private static long requestIdCounter = 0;

	// Manages all running requestss
	private Map<Long, Long> requests = new ConcurrentHashMap<Long, Long>();

	public DosPrevention()
	{
		MAX_REQUEST_TIME = Configuration.getInstance().getDosPreventionTime();

		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new Tick(), 0, 1000);
	}

	public final long startRequest()
	{
		long requestId = requestIdCounter++;
		requests.put(requestId, System.currentTimeMillis());
		return requestId;
	}

	public final void finishRequest(long requestId)
	{
		requests.remove(requestId);
	}

	private final void checkRequests()
	{
		// If time is negative - do nothing
		if (MAX_REQUEST_TIME == -1)
			return;

		long current = System.currentTimeMillis();
		for (long requestId : requests.keySet())
		{
			// Could be null in the meantime (Multithreading)
			Long time = requests.get(requestId);
			if (time == null)
				continue;

			if ((current - time) > MAX_REQUEST_TIME)
			{
				logger.fatal("Possible DOS attack or long running request detected - shutting down");
				System.exit(2);
			}
		}
	}

	class Tick extends TimerTask
	{
		@Override
		public void run()
		{
			checkRequests();
		}
	}
}
