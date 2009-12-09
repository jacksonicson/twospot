package org.prot.app.services.user;

public class MockUserService implements UserService
{
	private String currentUser = null;

	@Override
	public String getCurrentUser()
	{
		return currentUser;
	}

	@Override
	public String getLoginUrl(String redirectUrl, String cancelUrl)
	{
		return "/twospot/login.jsp?okUrl=" + redirectUrl + "&errUrl=" + cancelUrl;
	}

	@Override
	public void registerUser(String uid, String username)
	{
		currentUser = username;
	}

	@Override
	public void unregisterUser()
	{
		currentUser = null;
	}
}
