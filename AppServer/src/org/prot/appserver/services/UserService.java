package org.prot.appserver.services;

import javax.servlet.http.Cookie;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.HttpConnection;
import org.prot.appserver.config.Configuration;
import org.prot.util.Cookies;

public final class UserService
{
	private static final Logger logger = Logger.getLogger(UserService.class);

	private org.prot.controller.services.user.UserService proxy;

	public UserService(org.prot.controller.services.user.UserService proxy)
	{
		this.proxy = proxy;
	}

	public boolean getCurrentUser()
	{
		HttpConnection httpConnection = HttpConnection.getCurrentConnection();
		Cookie[] cookies = httpConnection.getRequest().getCookies();
		
		if(cookies == null)
			return false; 
		
		for (Cookie cookie : cookies)
		{
			if (cookie.getName().equals(Cookies.USER_ID))
			{
				return proxy.getCurrentUser(cookie.getValue());
			}
		}

		return false;
	}

	public String getLoginUrl(String redirectUrl)
	{
		return proxy.getLoginUrl(redirectUrl);
	}

	public void registerUser(String uid)
	{
		String token = Configuration.getInstance().getAuthenticationToken();
		proxy.registerUser(token, uid);
	}
}
