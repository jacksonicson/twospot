package org.prot.manager.services;

import org.apache.log4j.Logger;

public class UserServiceImpl implements UserService
{
	private static final Logger logger = Logger.getLogger(UserServiceImpl.class);
	
	@Override
	public String login(String appId, String username, String md5)
	{
		logger.info("LOGIN: " + appId);
		return "abc"; 
	}
}
