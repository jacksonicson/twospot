package org.prot.portal.filter;

import java.io.IOException;

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

	public void init()
	{
		service = UserServiceFactory.getUserService();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException
	{
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String uri = httpRequest.getRequestURI();
		if (uri.startsWith("/"))
			uri = uri.substring(1);

		String[] clean = { "docs", "index.htm", "login.htm", "start.htm", "loginHandler.htm",
				"registerHandler.htm", "loadTest.htm", "etc" };

		// Check if user is logged in
		String user = service.getCurrentUser();

		for (String test : clean)
		{
			// Special case here - if URI is empty its the start page which is
			// also clean
			if (uri.indexOf(test) != -1 || uri.isEmpty())
			{
				chain.doFilter(request, response);
				return;
			}
		}

		if (user == null)
		{
			logger.debug("Restricted access to: " + uri);
			response.getWriter().print("Access restricted");
			return;
		}

		chain.doFilter(request, response);
	}
}
