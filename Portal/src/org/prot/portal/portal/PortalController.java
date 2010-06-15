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
package org.prot.portal.portal;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.prot.app.services.user.UserService;
import org.prot.app.services.user.UserServiceFactory;
import org.prot.portal.services.AppService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class PortalController implements Controller
{
	private static final Logger logger = Logger.getLogger(PortalController.class);

	private AppService appService;
	
	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws Exception
	{
		UserService userService = UserServiceFactory.getUserService(); 
		String user = userService.getCurrentUser();
		
		Set<String> appIds = appService.getApplications(user);
		
		return new ModelAndView("portal", "appIds", appIds);
	}

	public void setAppService(AppService appService)
	{
		this.appService = appService;
	}
}
