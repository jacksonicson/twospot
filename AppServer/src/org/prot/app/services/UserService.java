package org.prot.app.services;

import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.servlet.http.Cookie;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.HttpConnection;
import org.prot.appserver.config.Configuration;
import org.prot.util.Cookies;

public final class UserService
{
	private static final Logger logger = Logger.getLogger(UserService.class);

	private org.prot.controller.services.user.UserService proxy;

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

	protected UserService(org.prot.controller.services.user.UserService proxy)
	{
		this.proxy = proxy;
	}

	public String getCurrentUser()
	{
		final String uid = searchUID();
		logger.debug("UID: " + uid);

		if (uid == null)
			return null;

		String o = AccessController.doPrivileged(new PrivilegedAction<String>()
		{
			@Override
			public String run()
			{
				return proxy.getCurrentUser(uid);
			}

		});

		return (String) o;
	}

	public String getLoginUrl(String redirectUrl)
	{
		return proxy.getLoginUrl(redirectUrl);
	}

	public void registerUser(final String uid, final String username)
	{
		final String token = Configuration.getInstance().getAuthenticationToken();

		AccessController.doPrivileged(new PrivilegedAction<String>()
		{
			@Override
			public String run()
			{
				proxy.registerUser(token, uid, username);
				return null;
			}

		});

	}

	public void unregisterUser()
	{
		final String uid = searchUID();
		// If there is no UID there is no user session
		if (uid == null)
			return;

		AccessController.doPrivileged(new PrivilegedAction<String>()
		{
			@Override
			public String run()
			{
				proxy.unregisterUser(uid);
				return null;
			}

		});
	}
}
