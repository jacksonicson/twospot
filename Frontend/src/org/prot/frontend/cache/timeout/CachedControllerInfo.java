package org.prot.frontend.cache.timeout;

import org.prot.manager.stats.ControllerInfo;

public class CachedControllerInfo extends ControllerInfo
{
	private static final long serialVersionUID = 2352999772518891579L;

	private long timestamp;

	private long queue;

	public CachedControllerInfo(ControllerInfo info)
	{
		super(info);
	}

	public long getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}

	public void increment()
	{
		this.queue++;
	}

	public synchronized void decrement()
	{
		this.queue--;
		assert (queue >= 0);
	}

	public synchronized long queue()
	{
		return queue;
	}
}
