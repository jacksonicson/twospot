package org.prot.portal.login;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class RegisterHandlerController extends SimpleFormController
{
	private static final Logger logger = Logger.getLogger(RegisterHandlerController.class);

	public RegisterHandlerController()
	{
		setCommandClass(RegisterCommand.class);
		setCommandName("registerCommand");
	}

//	protected ModelAndView onSubmit(
//			HttpServletRequest request,	HttpServletResponse response, Object command,	BindException errors)
//			throws Exception {
//
//		// TODO
//		
//		return null;
//	}
	
	protected void doSubmitAction(Object command) throws Exception {
		logger.info("Submitting registration form"); 
	}
}
