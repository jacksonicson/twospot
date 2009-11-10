package org.prot.frontend.cache;

public interface AppCache
{
	public void getController(String appId);
	
	public void deleteController(String appId); 
	
	public void updateCache(); 
}
