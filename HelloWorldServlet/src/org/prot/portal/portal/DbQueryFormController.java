package org.prot.portal.portal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.prot.portal.login.data.DataTablet;
import org.prot.portal.services.DbService;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

public class DbQueryFormController extends AbstractCommandController
{
	private DbBrowserController browserController;
	
	private DbService dbService;
	
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
		DbQueryCommand queryCommand = (DbQueryCommand)command;
		model.addAttribute("queryCommand", queryCommand);
		
		DataTablet tablet = dbService.getData(queryCommand.getTable());
		model.addAttribute("dataTableHead", tablet.getKeys());
		model.addAttribute("dataTablet", tablet.iterator());
		
		return mview; 
	}

	public void setBrowserController(DbBrowserController browserController)
	{
		this.browserController = browserController;
	}

	public void setDbService(DbService dbService)
	{
		this.dbService = dbService;
	}
}
