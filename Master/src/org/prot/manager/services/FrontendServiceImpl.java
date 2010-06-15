/*******************************************************************************
 * Copyright (c) 2010 Andreas Wolke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Wolke - initial API and implementation and initial documentation
 *******************************************************************************/
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
