package org.prot.frontend.cache.timeout;

import org.prot.manager.stats.ControllerInfo;

public class CachedControllerInfo extends ControllerInfo
{
	private static final long serialVersionUID = 2352999772518891579L;

	private long timestamp;

	private boolean blocked;

	public CachedControllerInfo(ControllerInfo info)
	{
		super(info);
	}

	public void setBlocked(boolean blocked)
	{
		this.blocked = blocked;
	}

	public boolean isBlocked()
	{
		return blocked;
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
