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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.prot.app.services.db.DbBrowserService;
import org.prot.app.services.db.DbBrowserServiceFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class DbBrowserController implements Controller {
	private static final Logger logger = Logger.getLogger(DbBrowserController.class);

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String appId = request.getParameter("appId");
		if (appId == null)
			return null;
		appId = appId.toLowerCase();
		String table = request.getParameter("table");

		DbQueryCommand queryCommand = new DbQueryCommand();
		queryCommand.setAppId(appId);
		queryCommand.setTable(table);

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("queryCommand", queryCommand);

		DbBrowserService dbService = DbBrowserServiceFactory.getDbBrowserService();

		List<String> tables = dbService.getTables(appId);
		model.put("tableList", tables);
		return new ModelAndView("dbBrowser", model);
	}
}
