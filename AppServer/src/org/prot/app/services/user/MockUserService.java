package org.prot.app.services.user;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
		String uri = "/twospot";
		try
		{
			uri += "/login.jsp?url=" + URLEncoder.encode(redirectUrl, "UTF-8");
			uri += "&cancel=" + URLEncoder.encode(cancelUrl, "UTF-8");
			return uri;

		} catch (UnsupportedEncodingException e)
		{
			return null;
		}
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
