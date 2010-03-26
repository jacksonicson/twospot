package org.prot.frontend.cache.timeout;

import org.prot.manager.stats.ControllerInfo;

public class CachedControllerInfo extends ControllerInfo
{
	private static final long serialVersionUID = 2352999772518891579L;

	private long timestamp;

	private Long blocked = null;

	public CachedControllerInfo(ControllerInfo info)
	{
		update(info);
	}

	public void setBlocked(boolean blocked)
	{
		if (blocked)
			this.blocked = System.currentTimeMillis();
		else
			this.blocked = null;
	}

	public Long getBlocked()
	{
		return blocked;
	}

	public boolean isBlocked()
	{
		return blocked != null;
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
