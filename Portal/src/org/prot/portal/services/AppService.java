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

import java.util.Set;

import org.apache.log4j.Logger;
import org.prot.app.services.user.UserServiceFactory;
import org.prot.portal.app.data.Application;
import org.prot.portal.login.data.AppDao;

public class AppService
{
	private static final Logger logger = Logger.getLogger(AppService.class);

	private AppDao appDao;

	public boolean existsApplication(String appId)
	{
		appId = appId.toLowerCase();
		Application app = appDao.loadApp(appId);
		return app != null;
	}

	public String getApplicationOwner(String appId)
	{
		appId = appId.toLowerCase();
		Application app = appDao.loadApp(appId);
		if (app == null)
			return null;

		return app.getOwner();
	}

	public void registerApplication(String appId)
	{
		org.prot.app.services.user.UserService userService = UserServiceFactory.getUserService();
		String owner = userService.getCurrentUser();

		appId = appId.toLowerCase();
		appDao.saveApp(appId, owner);
	}

	public void setAppDao(AppDao appDao)
	{
		this.appDao = appDao;
	}

	public Set<String> getApplications(String owner)
	{
		return appDao.getApps(owner);
	}
}
