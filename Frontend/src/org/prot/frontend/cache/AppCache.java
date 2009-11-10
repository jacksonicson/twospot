package org.prot.frontend.cache;

import org.prot.manager.config.ControllerInfo;

public interface AppCache
{
	public ControllerInfo getController(String appId);
	
	public void cacheController(String appId, ControllerInfo controller); 
	
	public void updateCache(); 
}
