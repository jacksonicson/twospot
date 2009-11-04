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
	
		Invocable invocable = pythonEngine.getInvocable();
		ScriptEngine engine = pythonEngine.getEngine(); 
		
		try
		{
			invocable.invokeFunction("sayHello");
			// engine.eval("")
			engine.eval("currentRequest = ModPythonRequest('')\n");
			Object o = engine.get("currentRequest");
			System.out.println("O: " + o); 
			
		} catch (ScriptException e)
		{
			e.printStackTrace();
		} catch (NoSuchMethodException e)
		{
			e.printStackTrace();
		} 
	}
}
