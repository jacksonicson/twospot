package org.prot.manager.services;

public interface UserService
{
	/**
	 * User login function. It returns an identifier which identifies the User
	 * for a certain time for the given application.
	 * 
	 * The lifetime of the returned identifier is unspecified. All identifiers
	 * are kept in the Controller's memory.
	 * 
	 * @param appId
	 *            The appId for the application
	 * @param username
	 *            The username
	 * @param md5
	 *            The MD5 encoded password
	 * @return An identifier which is used to identify the user. Null if the
	 *         login failed.
	 */
	public String login(String appId, String username, String md5);
}
