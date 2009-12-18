package org.prot.controller.stats;

import org.apache.log4j.Logger;

public class AppRequestStats
{
	private static final Logger logger = Logger.getLogger(AppRequestStats.class);

	private int index = 0;
	private long[] timestamps = new long[100];

	public void handle()
	{
		index = (index + 1) % timestamps.length;
		timestamps[index] = System.currentTimeMillis();
	}

	public double getRps()
	{
		long current = 0;
		long last = Long.MAX_VALUE;
		for (long time : timestamps)
		{
			if (time == 0)
				continue;

			if (time > current)
				current = time;

			if (time < last)
				last = time;
		}

		long diff = (current - last) / 1000 + 1;
		logger.debug("Time diff: " + diff);

		double rps = (double) timestamps.length / (double) (diff);
		logger.debug("RPS: " + rps);
		return rps;
	}
}
