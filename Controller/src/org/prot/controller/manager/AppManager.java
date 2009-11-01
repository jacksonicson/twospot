package org.prot.controller.manager;

public class AppManager {
	
	private AppRegistry registry; 
	
	private AppStarter starter; 
	
	public void setRegistry(AppRegistry registry) {
		this.registry = registry;
	}
	
	public void setStarter(AppStarter starter) {
		this.starter = starter; 
	}
	
	public boolean existsApp(String appId) {
		return this.registry.existsApp(appId); 
	}
	
	public AppInfo getAppInfo(String appId) {
		return this.registry.getAppInfo(appId); 
	}

	public AppInfo startApp(String appId) throws DuplicatedAppException {
		
		AppInfo appInfo = registry.registerApp(appId);
		starter.startApp(appInfo); 	
		
		return appInfo; 
	}
}
