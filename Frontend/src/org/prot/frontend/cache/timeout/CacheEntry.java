package org.prot.frontend.cache.timeout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.prot.manager.data.ControllerInfo;

public class CacheEntry
{
	private static final Logger logger = Logger.getLogger(CacheEntry.class);
	
	private List<CachedControllerInfo> controllers = new ArrayList<CachedControllerInfo>();

	private String appId;

	synchronized void addController(ControllerInfo controller)
	{
		CachedControllerInfo cController = new CachedControllerInfo(controller); 
		cController.setTimestamp(System.currentTimeMillis()); 
		controllers.add(cController);
	}

	synchronized void removeOlderThan(long threshold)
	{
		Set<CachedControllerInfo> toDel = new HashSet<CachedControllerInfo>();
		long currentTime = System.currentTimeMillis(); 
		for(CachedControllerInfo controller : controllers) 
		{
			if((currentTime - controller.getTimestamp()) > threshold) {
				toDel.add(controller); 
			}
		}
		
		for(CachedControllerInfo controller : toDel)
		{
			logger.debug("removing controller info"); 
			controllers.remove(controller);
		}
	}
	
	synchronized ControllerInfo pickController()
	{
		if(controllers.isEmpty())
			return null; 
		
		// Cycles through all controllers to balance the requests
		CachedControllerInfo info = controllers.get(0);
		controllers.remove(0);
		controllers.add(info);
		
		return info;
	}

	String getAppId()
	{
		return appId;
	}

	void setAppId(String appId)
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
