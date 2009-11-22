package org.prot.portal.services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;
import org.prot.portal.login.data.PlatformUser;
import org.prot.portal.login.data.UserDao;

public class UserService
{
	private static final Logger logger = Logger.getLogger(UserService.class);

	private UserDao userDao;

	public boolean existsUserId(String username)
	{
		PlatformUser user = userDao.getUser(username);
		return user != null;
	}

	private static final String MD5(String input)
	{
		MessageDigest algorithm;
		try
		{
			algorithm = MessageDigest.getInstance("MD5");
			algorithm.reset();
			algorithm.update(input.getBytes());
			String md5 = new String(algorithm.digest());
			return md5; 

		} catch (NoSuchAlgorithmException e)
		{
			logger.error("Could not create a MD5 hash of the password", e);
		}
		
		return null;
	}

	public void registerUser(PlatformUser user)
	{
		// TODO: Throw an exception
		if (existsUserId(user.getUsername()) == true)
			return;

		// MD5 hash password
		user.setMd5Password(MD5(user.getMd5Password()));

		// Register user in the application database
		userDao.saveUser(user);
	}

	public boolean checkCredentials(String username, String password)
	{
		// MD5 hash the password
		String md5 = MD5(password);

		// Load the user from the datastore
		PlatformUser user = userDao.getUser(username);
		if (user == null)
			return false;

		logger.info("comparing saved md5 and new md5");

		return (user.getMd5Password().equals(md5));
	}
	
	public void loginUser(String uid, String username)
	{
		// TODO: !!!
	}

	public void setUserDao(UserDao userDao)
	{
		this.userDao = userDao;
	}
}
