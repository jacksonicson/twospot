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
		// Ensure that username is case insensitive
		username = username.toLowerCase();

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
			byte[] byteMd5 = algorithm.digest();

			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < byteMd5.length; i++)
				hexString.append(Integer.toHexString(0xFF & byteMd5[i]));

			return hexString.toString();

		} catch (NoSuchAlgorithmException e)
		{
			logger.error("Could not create a MD5 hash of the password", e);
		}

		return null;
	}

	public PlatformUser getUser(String username)
	{
		assert (username != null);

		// Ensure that username is case insensitive
		username = username.toLowerCase();

		PlatformUser user = userDao.getUser(username);
		return user;
	}

	public void registerUser(PlatformUser user, String password)
	{
		// TODO: Throw an exception
		if (existsUserId(user.getUsername()) == true)
			return;

		// Ensure that username is case insensitive
		user.setUsername(user.getUsername().toLowerCase());

		// MD5 hash password
		user.setMd5Password(MD5(password));

		// Register user in the application database
		userDao.saveUser(user);
	}

	public boolean checkCredentials(String username, String password)
	{
		// MD5 hash the password
		String md5 = MD5(password);

		// Ensure that username is case insensitive
		username = username.toLowerCase();

		// Load the user from the datastore
		PlatformUser user = userDao.getUser(username);
		if (user == null)
			return false;

		logger.info("comparing saved md5: " + user.getMd5Password() + " and new md5: " + md5);
		return user.getMd5Password().equals(md5);
	}

	public void setUserDao(UserDao userDao)
	{
		this.userDao = userDao;
	}
}
