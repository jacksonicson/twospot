package org.prot.manager.balancing;

import java.util.Set;

import org.prot.manager.data.ControllerInfo;

public interface LoadBalancer
{
	public Set<ControllerInfo> selectController(String appId);
}
