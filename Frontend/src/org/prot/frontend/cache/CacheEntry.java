package org.prot.frontend.cache;

import java.util.ArrayList;
import java.util.List;

import org.prot.manager.config.ControllerInfo;

public class CacheEntry
{
	private long timestamp;

	private List<ControllerInfo> controllers = new ArrayList<ControllerInfo>();

	private String appId;

	public void addController(ControllerInfo controller)
	{
		controllers.add(controller);
	}

	public ControllerInfo pickController()
	{
		if(controllers.size() == 0)
			return null; 
		
		ControllerInfo info = controllers.get(0);
		controllers.remove(0);
		controllers.add(info);
		
		return info;
	}

	public long getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}

	public String getAppId()
	{
		return appId;
	}

	public void setAppId(String appId)
	{
		this.appId = appId;
	}

	public int hashCode()
	{
		return appId.hashCode();
	}

	public String toString()
	{
		return appId;
	}
}
