package org.prot.appserver.python;

import java.io.IOException;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.python.core.Py;
import org.python.core.PySystemState;

public class PythonHandler extends AbstractHandler
{
	private PythonEngine pythonEngine; 
	
	public void setPythonEngine(PythonEngine pythonEngine) {
		this.pythonEngine = pythonEngine; 
	}
	
	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{
		System.out.println("Handling request"); 
	
		ScriptEngine engine = pythonEngine.getEngine(); 
		
		try
		{
			RequestWrapper transfer = new RequestWrapper(baseRequest, request, response);
			engine.eval("os.environ['DJANGO_SETTINGS_MODULE'] = 'blub.settings'");
			engine.put("transfer", transfer);
			engine.eval("handler(transfer)");
			
			System.out.println("hm");
			transfer.close(); 
			
		} catch (ScriptException e)
		{
			e.printStackTrace();
		} 
	}
}
