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
		if(appInfos.containsKey(appId))
			throw new DuplicatedAppException(appId); 
		
		AppInfo appInfo = new AppInfo();
		appInfo.setAppId(appId);
		appInfo.setPort(getPort());
		putApp(appInfo);
		
		return appInfo; 
	}
	
	private void readPidFile() {
		
	}
}
