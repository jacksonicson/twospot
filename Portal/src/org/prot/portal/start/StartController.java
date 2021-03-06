/*******************************************************************************
 * Copyright (c) 2010 Andreas Wolke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Wolke - initial API and implementation and initial documentation
 *******************************************************************************/
package org.prot.portal.start;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.prot.app.services.log.LogService;
import org.prot.app.services.log.LogServiceFactory;
import org.prot.app.services.user.UserService;
import org.prot.app.services.user.UserServiceFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.InternalResourceView;

public class StartController implements Controller
{
	private static final Logger logger = Logger.getLogger(StartController.class);

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws Exception
	{
		LogService log = LogServiceFactory.getLogService();
		if(log == null)
		{
			logger.error("Log is null"); 
		}
		
		log.debug("Logging: " + System.currentTimeMillis());

		// Check if the user has a session
		UserService userService = UserServiceFactory.getUserService();
		String user = userService.getCurrentUser();

		// If the user is not logged in
		if (user == null)
			return new ModelAndView("start");

		// if the user is loged go to the portal
		return new ModelAndView(new InternalResourceView("/portal.htm"));
	}
}
