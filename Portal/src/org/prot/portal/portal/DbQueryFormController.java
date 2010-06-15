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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.prot.app.services.db.DbBrowserService;
import org.prot.app.services.db.DbBrowserServiceFactory;
import org.prot.jdo.storage.messages.EntityMessage;
import org.prot.jdo.storage.messages.IndexMessage;
import org.prot.jdo.storage.messages.types.IStorageProperty;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

public class DbQueryFormController extends AbstractCommandController {
	private DbBrowserController browserController;

	private static final Logger logger = Logger.getLogger(DbBrowserController.class);

	public DbQueryFormController() {
		setCommandClass(DbQueryCommand.class);
		setCommandName("queryCommand");
	}

	@Override
	protected ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object command,
			BindException errors) throws Exception {
		ModelAndView mview = browserController.handleRequest(request, response);
		ModelMap model = mview.getModelMap();

		DbQueryCommand queryCommand = (DbQueryCommand) command;
		model.addAttribute("queryCommand", queryCommand);

		logger.debug("Table: " + queryCommand.getTable());

		DbBrowserService dbService = DbBrowserServiceFactory.getDbBrowserService();
		List<EntityMessage> list = dbService.getTableData(queryCommand.getAppId(), queryCommand.getTable());

		StringBuffer out = new StringBuffer();
		out.append("<table border='1'>");
		int index = 0;
		for (EntityMessage msg : list) {
			if (msg == null)
				continue;

			out.append("<tr>");

			out.append("<td>");
			out.append(index++);
			out.append("</td>");

			out.append("<td>");
			out.append(msg.getClassName());
			out.append("</td>");

			out.append("<td><table cellspacing='10'>");

			for (IndexMessage imsg : msg.getIndexMessages()) {
				if (imsg == null)
					continue;

				out.append("<tr>");

				out.append("<td>");
				out.append(imsg.getFieldNumber());
				out.append("</td>");

				out.append("<td>");
				out.append(imsg.getFieldName());
				out.append("</td>");

				out.append("<td>");
				out.append(imsg.getFieldType());
				out.append("</td>");

				out.append("<td>");
				IStorageProperty p = msg.getProperty(imsg.getFieldName());
				if (p != null) {
					out.append(p.getValue().toString());
				} else {
					out.append("NULL");
				}
				out.append("</td>");

				out.append("</tr>");
			}

			out.append("</table></td>");
			out.append("</tr>");
		}
		out.append("</table>");

		model.addAttribute("data", out.toString());

		return mview;
	}

	public void setBrowserController(DbBrowserController browserController) {
		this.browserController = browserController;
	}

}
