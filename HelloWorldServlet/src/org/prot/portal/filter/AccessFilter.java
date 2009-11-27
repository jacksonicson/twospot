package org.prot.portal.filter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.prot.app.services.user.UserService;
import org.prot.app.services.user.UserServiceFactory;
import org.springframework.web.filter.GenericFilterBean;

public class AccessFilter extends GenericFilterBean
{
	private static final Logger logger = Logger.getLogger(AccessFilter.class);
	
	private UserService service;  
	
	private final static Set<String> withoutLogin = new HashSet<String>();
	
	static {
		withoutLogin.add("start.htm");
		withoutLogin.add("login.htm");
		withoutLogin.add("loginHandler.htm");
		withoutLogin.add("registerHandler.htm");
	}
	
	public void init()
	{
		service = UserServiceFactory.getUserService();
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException
	{
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		String uri = httpRequest.getRequestURI(); 
		int index = uri.lastIndexOf("/");
		if(index != -1)
			uri = uri.substring(index + 1); 
		
		String user = service.getCurrentUser();
		logger.info("User: " + user);
		
//		if(user == null)
//		{
//			if(withoutLogin.contains(uri)) 
//				chain.doFilter(request, response);
//			else
//			{
//				logger.info("Restricted access to: " + uri);
//				response.getWriter().print("Access restricted"); 
//				return; 
//			}
//		}
		
		chain.doFilter(request, response);
	}
}
