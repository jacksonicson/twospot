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
package org.prot.portal.services;

import org.apache.log4j.Logger;
import org.prot.app.services.platform.PlatformService;
import org.prot.app.services.platform.PlatformServiceFactory;

public class DeploymentService
{
	private static final Logger logger = Logger.getLogger(DeploymentService.class);

	public String announceDeployment(String appId, String version)
	{
		PlatformService platformService = PlatformServiceFactory.getPlatformService();
		return platformService.announceApp(appId, version);
	}

	public void deployApplication(String appId, String version)
	{
		PlatformService platformService = PlatformServiceFactory.getPlatformService();
		platformService.appDeployed(appId, version);
	}
}
