package org.prot.frontend.cache.timeout;

import org.prot.manager.stats.ControllerInfo;

public class CachedControllerInfo extends ControllerInfo
{
	private static final long serialVersionUID = 2352999772518891579L;

	private long timestamp;

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
}
