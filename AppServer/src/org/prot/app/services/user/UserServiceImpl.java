package org.prot.app.services.user;

import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.servlet.http.Cookie;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.HttpConnection;
import org.prot.appserver.config.Configuration;
import org.prot.util.Cookies;

public final class UserServiceImpl implements UserService
{
	private static final Logger logger = Logger.getLogger(UserServiceImpl.class);

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
				String uid = cookie.getValue();
				return uid;
			}
		}

		return null;
	}

	protected UserServiceImpl(org.prot.controller.services.user.UserService proxy)
	{
		this.proxy = proxy;
	}

	public String getCurrentUser()
	{
		final String uid = searchUID();
		if (uid == null)
			return null;

		String user = AccessController.doPrivileged(new PrivilegedAction<String>()
		{
			@Override
			public String run()
			{
				String user = proxy.getCurrentUser(uid);
				return user;
			}
		});

		return (String) user;
	}

	public String getLoginUrl(String redirectUrl, String cancelUrl)
	{
		return proxy.getLoginUrl(redirectUrl, cancelUrl);
	}

	public void registerUser(final String uid, final String username)
	{
		final String token = Configuration.getInstance().getAuthenticationToken();
		assert (token != null);

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

		// Delete the UID-Cookie
		HttpConnection httpConnection = HttpConnection.getCurrentConnection();
		Cookie[] cookies = httpConnection.getRequest().getCookies();
		for (Cookie cookie : cookies)
		{
			if (cookie.getName().equals(Cookies.USER_ID))
			{
				cookie.setMaxAge(0);
				cookie.setValue(null);
				httpConnection.getResponse().addCookie(cookie);
				break;
			}
		}
	}
}
