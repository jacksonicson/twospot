package org.prot.frontend.cache;

import org.prot.manager.stats.ControllerInfo;

public final class CacheResult
{
	private final ControllerInfo controllerInfo;

	private final String appId;  

	public CacheResult(ControllerInfo controllerInfo, String appId)
	{
		this.controllerInfo = controllerInfo;
		this.appId = appId;
	}

	public ControllerInfo getControllerInfo()
	{
		return controllerInfo;
	}
	
	public String getAppId()
	{
		return appId;
	}
}
