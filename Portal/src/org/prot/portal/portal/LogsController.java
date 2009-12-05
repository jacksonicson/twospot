package org.prot.portal.portal;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.prot.app.services.log.LogServiceFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class LogsController implements Controller
{
	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws Exception
	{
		String appId = request.getParameter("appId");

		List<String> messages = LogServiceFactory.getLogService().getMessages(-1);

		return new ModelAndView("logs", "logMessages", messages);
	}

}
