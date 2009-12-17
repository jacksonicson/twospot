package org.prot.manager.services;

import java.util.Set;

import org.apache.log4j.Logger;
import org.prot.manager.balancing.LoadBalancer;
import org.prot.manager.stats.ControllerInfo;

public class FrontendServiceImpl implements FrontendService
{
	private static final Logger logger = Logger.getLogger(FrontendServiceImpl.class);

	private LoadBalancer loadBalancer;

	@Override
	public Set<ControllerInfo> selectController(String appId)
	{
		logger.debug("Choosing Controller for AppId: " + appId);
		return loadBalancer.selectController(appId);
	}

	public void setLoadBalancer(LoadBalancer loadBalancer)
	{
		this.loadBalancer = loadBalancer;
	}
}
