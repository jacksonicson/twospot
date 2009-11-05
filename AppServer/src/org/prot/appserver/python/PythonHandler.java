package org.prot.appserver.python;

import java.io.IOException;

import javax.script.ScriptEngine;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class PythonHandler extends AbstractHandler
{
	private ScriptEngine engine;

	public void setPythonEngine(PythonEngine pythonEngine)
	{
		this.engine = pythonEngine.getEngine();
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{
		try
		{
			RequestAdapter adapter = new RequestAdapter(baseRequest, request, response);
			engine.put("transfer", adapter);
			engine.eval("handler(transfer)");

			adapter.close();

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
