package org.prot.portal.services;

import java.util.Set;

import org.prot.app.services.UserService;
import org.prot.app.services.UserServiceFactory;
import org.prot.portal.app.data.Application;
import org.prot.portal.login.data.AppDao;

public class AppService
{
	private AppDao appDao;

	public boolean existsApplication(String appId)
	{
		Application app = appDao.loadApp(appId);
		return app != null;
	}

	public void registerApplication(String appId)
	{
		UserService userService = UserServiceFactory.getUserService();
		String owner = userService.getCurrentUser();
		
		appDao.saveApp(appId, owner);
	}

	public void setAppDao(AppDao appDao)
	{
		this.appDao = appDao;
	}
	
	public Set<String> getApplications(String owner)
	{
		return null;
	}
}
