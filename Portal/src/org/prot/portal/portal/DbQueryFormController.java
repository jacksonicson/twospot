package org.prot.portal.portal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.prot.app.services.db.DbBrowserService;
import org.prot.app.services.db.DbBrowserServiceFactory;
import org.prot.controller.services.db.DataTablet;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

public class DbQueryFormController extends AbstractCommandController
{
	private DbBrowserController browserController;

	public DbQueryFormController()
	{
		setCommandClass(DbQueryCommand.class);
		setCommandName("queryCommand");
	}

	@Override
	protected ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object command,
			BindException errors) throws Exception
	{
		ModelAndView mview = browserController.handleRequest(request, response);
		ModelMap model = mview.getModelMap();
		DbQueryCommand queryCommand = (DbQueryCommand) command;
		model.addAttribute("queryCommand", queryCommand);

		DbBrowserService dbService = DbBrowserServiceFactory.getDbBrowserService();

		DataTablet tablet = dbService.getTableData(queryCommand.getTable(), "", 100);
		model.addAttribute("dataTableHead", tablet.getKeys());
		model.addAttribute("dataTablet", tablet.iterator());

		return mview;
	}

	public void setBrowserController(DbBrowserController browserController)
	{
		this.browserController = browserController;
	}

}
