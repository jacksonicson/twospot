package org.prot.appserver.python;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

import javax.script.ScriptEngine;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.prot.appserver.Configuration;
import org.prot.appserver.app.AppInfo;
import org.prot.appserver.app.WebConfiguration;

public class PythonHandler extends AbstractHandler
{
	private static final Logger logger = Logger.getLogger(PythonHandler.class);
	
	private ScriptEngine engine;

	public void setPythonEngine(PythonEngine pythonEngine)
	{
		this.engine = pythonEngine.getEngine();
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{
		// Get the URI
		String uri = baseRequest.getUri().toString(); 
		
		// Decide which python-file to execute
		Configuration configuration = Configuration.getInstance(); 
		AppInfo appInfo = configuration.getAppInfo(); 
		Set<WebConfiguration> configurations = appInfo.getWebConfigurations();
		String pythonFile = null; 
		for(WebConfiguration config : configurations) {
			pythonFile = config.matches(uri);
			if(pythonFile != null)
				break; 
		}
		
		// Error-Handling if no python-file found
		if(pythonFile == null) {
			logger.error("TODO: no python file has been found");
			return; 
		}
		
		try
		{
			// Execute the choosen python-file
			FileReader reader = new FileReader(new File(configuration.getAppDirectory() + "/WEB-INF/python/" + pythonFile)); 
			engine.eval(reader);
			
			
			
			/*RequestAdapter adapter = new RequestAdapter(baseRequest, request, response);
			engine.put("transfer", adapter);
			engine.eval("handler(transfer)");

			adapter.close(); */

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
