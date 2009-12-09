package org.prot.app.services.user;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;

import org.eclipse.jetty.server.HttpConnection;
import org.prot.util.Cookies;

public class MockUserService implements UserService
{
	private String currentUser = null;
	private String uid = null;

	private String searchUID()
	{
		HttpConnection httpConnection = HttpConnection.getCurrentConnection();
		Cookie[] cookies = httpConnection.getRequest().getCookies();

		// If there is no cookie there is no active session
		if (cookies == null)
			return null;

		// Search the cookie named USER_ID
		for (Cookie cookie : cookies)
		{
			if (cookie.getName().equals(Cookies.USER_ID))
			{
				return cookie.getValue();
			}
		}

		return null;
	}

	@Override
	public String getCurrentUser()
	{
		String uid = searchUID();
		if (uid == null)
			return null;

		if (this.uid == null)
			return null;

		if (this.uid.equals(uid) == false)
			return null;

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
		this.uid = uid;
		currentUser = username;
	}

	@Override
	public void unregisterUser()
	{
		this.uid = null;
		currentUser = null;
	}
}
