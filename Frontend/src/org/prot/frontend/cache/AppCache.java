package org.prot.frontend.cache;

import org.prot.manager.data.ControllerInfo;

public interface AppCache
{
	public ControllerInfo getController(String appId);

	public void cacheController(String appId, ControllerInfo controller);
}
