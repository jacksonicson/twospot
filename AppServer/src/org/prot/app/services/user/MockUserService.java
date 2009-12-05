package org.prot.app.services.user;

import org.prot.controller.services.user.UserService;

public class MockUserService implements UserService
{
	private String uid = null;
	private String username = null;

	@Override
	public String getCurrentUser(String uid)
	{
		// Check if the UID matches
		if (this.uid.equals(uid))
			return this.username;

		return null;
	}

	@Override
	public String getLoginUrl(String redirectUrl, String cancelUrl)
	{
		// TODO - AppServer must implement some mock login system
		return null;
	}

	@Override
	public void registerUser(String token, String uid, String username)
	{
		// Don't check the token - this is is only a mock implementation
		this.uid = uid;
		this.username = username;
	}

	@Override
	public void unregisterUser(String token, String uid)
	{
		// Do nothing - user applications cannot executed privileged operations
		this.uid = null;
		this.username = null;

		return;
	}

}
