package org.prot.frontend.cache;

import org.prot.manager.data.ControllerInfo;

public interface AppCache
{
	public ControllerInfo getController(String appId);
}
