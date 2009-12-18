package org.prot.controller.stats;

public class RpsCounter
{
	private int index = 0;
	private long[] timestamps = new long[100];

	public void count()
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
		double rps = (double) timestamps.length / (double) (diff);
		return rps;
	}

}
