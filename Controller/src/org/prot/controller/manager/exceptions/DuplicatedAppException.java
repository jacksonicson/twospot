package org.prot.controller.manager.exceptions;

import org.prot.controller.manager.AppInfo;

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
