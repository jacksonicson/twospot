package org.prot.portal.portal;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.prot.app.services.db.DbBrowserService;
import org.prot.app.services.db.DbBrowserServiceFactory;
import org.prot.jdo.storage.messages.EntityMessage;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

public class DbQueryFormController extends AbstractCommandController {
	private DbBrowserController browserController;

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

		DbBrowserService dbService = DbBrowserServiceFactory.getDbBrowserService();
		List<EntityMessage> list = dbService.getTableData(queryCommand.getAppId(), queryCommand.getTable());
		model.addAttribute("data", list);

		return mview;
	}

	public void setBrowserController(DbBrowserController browserController) {
		this.browserController = browserController;
	}

}
