package org.prot.controller.manager;

public class DuplicatedAppException extends Exception
{
	private AppInfo appInfo; 
	
	public DuplicatedAppException(AppInfo appInfo) {
		this.appInfo = appInfo; 
	}
	
	public String toString() {
		return "Duplicated AppId: " + appInfo.getAppId(); 
	}
}
