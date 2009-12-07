package org.prot.portal.services;

import java.util.Set;

import org.apache.log4j.Logger;
import org.prot.app.services.user.UserService;
import org.prot.app.services.user.UserServiceFactory;
import org.prot.portal.app.data.Application;
import org.prot.portal.login.data.AppDao;

public class AppService
{
	private static final Logger logger = Logger.getLogger(AppService.class);
	
	private AppDao appDao;

	public boolean existsApplication(String appId)
	{
		Application app = appDao.loadApp(appId);
		return app != null;
	}
	
	public String getApplicationOwner(String appId)
	{
		Application app = appDao.loadApp(appId);
		if(app == null)
			return null;
			
		return app.getOwner();
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
		return appDao.getApps(owner);
	}
}
