package org.prot.appserver.python;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpRetryException;
import java.util.Enumeration;
import java.util.Set;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.HttpConnection;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
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
			// Establish an IO channel
			HttpConnection httpConnection = HttpConnection.getCurrentConnection();
			Request srcRequest = httpConnection.getRequest();
			Response srcResponse = httpConnection.getResponse();
			WsgIO io = new WsgIO(srcRequest, srcResponse);

			// Load all header fields into the os.environ
			engine.eval("import os"); 
			for(Enumeration<String> names = baseRequest.getHeaderNames(); names.hasMoreElements(); ) {
				String name = names.nextElement();
				String value = baseRequest.getHeader(name); 
				
				System.out.println("name: " + name + " value: " + value);
				engine.eval("os.environ['" + name + "'] = '" + value + "'");
			}
			
			engine.eval("os.environ['" + "REQUEST_METHOD" + "'] = '" + baseRequest.getMethod() + "'");
			engine.eval("os.environ['" + "SERVER_NAME" + "'] = '" + baseRequest.getServerName() + "'");
			engine.eval("os.environ['" + "SERVER_PORT" + "'] = '" + baseRequest.getServerPort() + "'");
			engine.eval("os.environ['" + "PATH_INFO" + "'] = '" + baseRequest.getUri() + "'");
			
			
			Bindings bindings = engine.getBindings(ScriptContext.GLOBAL_SCOPE);
			bindings.put("wsgio_in", io); 
			
						
			// Execute the choosen python-file
			FileReader reader = new FileReader(new File(configuration.getAppDirectory() + "/WEB-INF/python/" + pythonFile)); 
			engine.eval(reader);
			
			
			response.getOutputStream().close();
			baseRequest.setHandled(true); 
			
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
