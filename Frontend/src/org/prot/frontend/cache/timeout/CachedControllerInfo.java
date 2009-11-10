package org.prot.frontend.cache.timeout;

import org.prot.manager.config.ControllerInfo;

public class CachedControllerInfo extends ControllerInfo
{
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
