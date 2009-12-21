package org.prot.frontend.cache;

import org.prot.manager.stats.ControllerInfo;

public interface AppCache
{
	public ControllerInfo getController(String appId);

	public void staleController(String address);
}
