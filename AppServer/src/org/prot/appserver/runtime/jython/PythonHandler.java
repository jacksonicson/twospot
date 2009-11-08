package org.prot.appserver.runtime.jython;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Stack;

import javax.script.ScriptEngineManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.HttpConnection;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.prot.appserver.config.Configuration;
import org.python.core.Py;
import org.python.core.PyCode;
import org.python.core.PyDictionary;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

public class PythonHandler extends AbstractHandler
{
	private static final Logger logger = Logger.getLogger(PythonHandler.class);

	// private ScriptEngine engine;
	ScriptEngineManager engineManager = new ScriptEngineManager();

	PySystemState engineSys = new PySystemState();

	public void setPythonEngine(PythonEngine pythonEngine)
	{
		// this.engine = // pythonEngine.getEngine();
		Configuration config = Configuration.getInstance();
		engineSys.path.append(Py.newString(config.getPythonLibs()));
		engineSys.path.append(Py.newString(config.getDjangoLibs()));
		engineSys.path.append(Py.newString(config.getAppDirectory() + "/WEB-INF/python"));

		// Python file from the server
		engineSys.path.append(Py.newString(new File("/bin").getAbsolutePath()));

		Py.setSystemState(engineSys);

		//		
		// try
		// {
		// } catch (ScriptException e1)
		// {
		// e1.printStackTrace();
		// }

		String pythonFile = "hellopython/test.py";
		Configuration configuration = Configuration.getInstance();
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(new File(configuration
					.getAppDirectory()
					+ "/WEB-INF/python/" + pythonFile)));
			String line = "";
			while ((line = reader.readLine()) != null)
				file += line + "\n";


		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	String file = "";

	PyCode cs;

	Stack<PythonInterpreter> engines = new Stack<PythonInterpreter>();

	public PythonInterpreter giveRuntime()
	{
		synchronized (engines)
		{
			PythonInterpreter e = null;
			if (engines.isEmpty())
			{
				long time = System.currentTimeMillis();
				
				Py.setSystemState(engineSys);
				e = new PythonInterpreter();
				System.out.println("Engine: " + e);
				
				time = System.currentTimeMillis() - time;
				System.out.println("Time used for engine creation: " + time);
			} else
			{
				e = engines.pop();
				// e = engines.peek(); 
			}

			return e;
		}
	}

	public void freeRuntime(PythonInterpreter e)
	{
		synchronized (engines)
		{
			engines.push(e);
		}
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{

		PythonInterpreter engine = giveRuntime();

		// Get the URI
		String uri = baseRequest.getUri().toString();

		// Decide which python-file to execute
		// Configuration configuration = Configuration.getInstance();
		// AppInfo appInfo = configuration.getAppInfo();
		// Set<WebConfiguration> configurations =
		// appInfo.getWebConfigurations();
		// String pythonFile = null;
		// for(WebConfiguration config : configurations) {
		// pythonFile = config.matches(uri);
		// if(pythonFile != null)
		// break;
		// }

		String pythonFile = "hellopython/test.py";

		// Error-Handling if no python-file found
		if (pythonFile == null)
		{
			logger.error("TODO: no python file has been found");
			return;
		}

		try
		{

			long time = System.currentTimeMillis();

			// Py.setSystemState(engineSys);
			// engine = engineManager.getEngineByName("jython");
			// engine.eval("import os");

			time = System.currentTimeMillis() - time;
			// System.out.println("TIME IMPORT: " + time);
			time = System.currentTimeMillis();

			// Establish an IO channel
			HttpConnection httpConnection = HttpConnection.getCurrentConnection();
			Request srcRequest = httpConnection.getRequest();
			Response srcResponse = httpConnection.getResponse();
			WsgiChannel io = new WsgiChannel(srcRequest, srcResponse);

			time = System.currentTimeMillis() - time;
			// System.out.println("TIME 0: " + time);
			time = System.currentTimeMillis();

			String puffer = "";
			PyDictionary dict = new PyDictionary();

			for (Enumeration<String> names = baseRequest.getHeaderNames(); names.hasMoreElements();)
			{
				String name = names.nextElement();
				String value = baseRequest.getHeader(name);

				// System.out.println("name: " + name + " value: " + value);
				dict.put(name, value);
				// puffer += ("os.environ['" + name + "'] = '" + value + "'") +
				// "\n";
			}

			dict.put("REQUEST_METHOD", baseRequest.getMethod());
			dict.put("SERVER_NAME", baseRequest.getServerName());
			dict.put("SERVER_PORT", baseRequest.getServerPort());
			dict.put("PATH_INFO", baseRequest.getUri());

			// puffer += ("os.environ['" + "REQUEST_METHOD" + "'] = '" +
			// baseRequest.getMethod() + "'") + "\n";
			// puffer += ("os.environ['" + "SERVER_NAME" + "'] = '" +
			// baseRequest.getServerName() + "'") + "\n";
			// puffer += ("os.environ['" + "SERVER_PORT" + "'] = '" +
			// baseRequest.getServerPort() + "'") + "\n";
			// puffer += ("os.environ['" + "PATH_INFO" + "'] = '" +
			// baseRequest.getUri() + "'") + "\n";

			// engine.put("wsgio_in", io);

			// puffer += ("from wsgiAdapter import set") + "\n";
			// puffer += ("set(wsgio_in)") + "\n";

			// Invocable in = ((Invocable)engine);
			// in.invokeFunction("set", io, dict);

			// System.out.println("IO: " + io);

			engine.set("headers", dict);
			engine.set("wsgiChannel", io);
			engine.set("threadName", Thread.currentThread().getName());
			
			// engine.eval(puffer);

			time = System.currentTimeMillis() - time;
			// System.out.println("TIME 1: " + time);
			time = System.currentTimeMillis();

			// Execute the choosen python-file
			// FileReader reader = new FileReader(new
			// File(configuration.getAppDirectory() + "/WEB-INF/python/" +
			// pythonFile));

			// engine.eval(reader);

			// Use compiled file version
			if (cs == null)
			{
				cs = engine.compile(file);
			}
			engine.exec(cs); 

			// engine.eval(file);

			time = System.currentTimeMillis() - time;
			// System.out.println("TIME 2: " + time);
			time = System.currentTimeMillis();

			response.getOutputStream().close();
			baseRequest.setHandled(true);

			/*
			 * RequestAdapter adapter = new RequestAdapter(baseRequest, request,
			 * response); engine.put("transfer", adapter);
			 * engine.eval("handler(transfer)");
			 * 
			 * adapter.close();
			 */

		} catch (Exception e)
		{
			e.printStackTrace();
		}

		freeRuntime(engine);
	}
}