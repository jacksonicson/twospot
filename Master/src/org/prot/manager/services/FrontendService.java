package org.prot.manager.services;

import java.util.Set;

import org.prot.manager.data.ControllerInfo;

public interface FrontendService
{
	/**
	 * @param appId
	 * @return null if there was an error or there are no controllers available
	 */
	public Set<ControllerInfo> selectController(String appId);
}
