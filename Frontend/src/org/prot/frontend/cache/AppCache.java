package org.prot.frontend.cache;


public interface AppCache
{
	public CacheResult getController(String appId);

	public void release(CacheResult result);
	
	public void staleController(String address);
}
