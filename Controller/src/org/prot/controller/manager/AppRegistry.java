package org.prot.controller.manager;

import java.util.Hashtable;
import java.util.Stack;

public class AppRegistry {

	private final int startPort = 9090;
	private int currentPort = startPort; 
	
	private Stack<Integer> freePorts = new Stack<Integer>(); 
	
	private Hashtable<String, AppInfo> appInfos = new Hashtable<String, AppInfo>();
	
	private void putApp(AppInfo appInfo) {
		this.appInfos.put(appInfo.getAppId(), appInfo);
	}
	
	private int getPort() {
		if(freePorts.isEmpty()) {
			return this.currentPort++; 
		}
		
		return freePorts.peek(); 
	}

	public boolean existsApp(String appId) {
		return appInfos.containsKey(appId); 
	}
	
	public AppInfo getAppInfo(String appId) {
		return appInfos.get(appId); 
	}
	
	public AppInfo registerApp(String appId) throws DuplicatedAppException {
		AppInfo appInfo = appInfos.get(appId);
		if(appInfo != null)
		{
			if(appInfo.isStale() == false)
				throw new DuplicatedAppException(appId);
		} else {
			appInfo = new AppInfo();
			appInfo.setAppId(appId);
			putApp(appInfo);
		}
		
		appInfo.setPort(getPort());
		
		return appInfo; 
	}
	
	private void readPidFile() {
		
	}
}
