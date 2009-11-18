package org.prot.controller.services;

import java.util.Collection;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

public class UserServiceImpl implements UserService
{
	private PersistenceManagerFactory pmFactory;
	private PersistenceManager persistenceManager;
	
	public UserServiceImpl()
	{
		pmFactory = JDOHelper.getPersistenceManagerFactory("etc/jdoDefault.properties");
		persistenceManager = pmFactory.getPersistenceManager();
	}
	
	@Override
	public void getCurrentUser()
	{
		// Query database
		Query query = persistenceManager.newQuery(UserSession.class);
		Collection<UserSession> result = (Collection<UserSession>)query.execute();
		for(UserSession session : result) {
			System.out.println("Session test: " + session.getSessionId());
		}
		
		System.out.println("Done"); 
	}

	@Override
	public String getLoginUrl()
	{
		System.out.println("get login url");
		return "http://www.andmedia.de";
	}

}