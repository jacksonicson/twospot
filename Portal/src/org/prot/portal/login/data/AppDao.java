package org.prot.portal.login.data;

import java.util.Set;

import org.prot.portal.app.data.Application;

public interface AppDao
{
	public Application loadApp(String appId); 
	
	public Set<String> getApps(String owner);
	
	public void saveApp(String appId, String owner);
}
