package org.prot.helloworldservlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.prot.appserver.services.UserService;
import org.prot.appserver.services.UserServiceFactory;

@SuppressWarnings("serial")
public class HelloWorldServlet extends HttpServlet
{
	private static final Logger logger = Logger.getLogger(HelloWorldServlet.class);
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		UserService service = UserServiceFactory.getUserService();
		logger.info("service erhalten"); 
		
		boolean value = service.getCurrentUser(); 
		
		resp.getWriter().print("done: " + value);
		
		if(service.getCurrentUser() == false)
			resp.sendRedirect(service.getLoginUrl("http://localhost:8080/portal/helloworld"));
	}
}
