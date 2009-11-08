package org.prot.appserver.runtime;

import org.prot.appserver.app.AppInfo;

public interface AppRuntime
{
	public String getIdentifier();
	
	public void loadConfiguration(AppInfo appInfo);
	
	public void launch(); 
}
