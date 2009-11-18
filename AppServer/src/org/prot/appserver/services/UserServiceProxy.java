package org.prot.appserver.services;

import javax.servlet.http.Cookie;

import org.eclipse.jetty.server.HttpConnection;
import org.prot.controller.services.user.PrivilegedUserService;
import org.prot.util.Cookies;

public class UserServiceProxy
{
	private PrivilegedUserService userService; 
	
	public UserServiceProxy(PrivilegedUserService userService)
	{
		this.userService = userService;
	}
	
	public boolean getCurrentUser()
	{
		// Get the current request
		HttpConnection con = HttpConnection.getCurrentConnection();
		Cookie[] cookies = con.getRequest().getCookies();
		for(Cookie cookie : cookies)
		{
			if(cookie.getName().equals(Cookies.USER_ID))
			{
				return userService.getCurrentUser(cookie.getValue());
			}
		}
		
		return false;
	}

	public String getLoginUrl(String redirect)
	{
		return "http://localhost:8080/portal/login?url=" + redirect; 
	}
	
	public void registerSession(String token, String session)
	{
		userService.registerSession(token, session);
	}
}
