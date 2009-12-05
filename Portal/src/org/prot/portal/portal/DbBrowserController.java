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

public class DbBrowserController implements Controller
{
	private static final Logger logger = Logger.getLogger(DbBrowserController.class);

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws Exception
	{
		String appId = request.getParameter("appId");

		DbQueryCommand queryCommand = new DbQueryCommand();
		queryCommand.setAppId(appId);

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("queryCommand", queryCommand);

		DbBrowserService dbService = DbBrowserServiceFactory.getDbBrowserService();

		List<String> tables = dbService.getTables(appId);
		model.put("tableList", tables);
		return new ModelAndView("dbBrowser", model);
	}
}
