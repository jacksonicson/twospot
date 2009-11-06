package org.prot.appserver.python;

import java.io.InputStream;
import java.io.InputStreamReader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.prot.appserver.Configuration;
import org.python.core.Py;
import org.python.core.PySystemState;

public class PythonEngine
{
	private ScriptEngine engine;

	public ScriptEngine getEngine()
	{
		return this.engine;
	}

	public void start()
	{
		// Create a new engine manager
		ScriptEngineManager engineManager = new ScriptEngineManager();

		// System state must be set before creating the engine
		// The SystemState can be passed as a parameter if the
		// PythonInterpreter-Class is used
		Configuration config = Configuration.getInstance(); 
		
		PySystemState engineSys = new PySystemState();
		engineSys.path.append(Py.newString(config.getPythonLibs()));
		engineSys.path.append(Py.newString(config.getDjangoLibs()));
		engineSys.path.append(Py.newString(config.getAppDirectory() + "/WEB-INF/python"));
		Py.setSystemState(engineSys);

		// Create a new engine
		engine = engineManager.getEngineByName("jython");

		try
		{
			// Load scripts
//			String[] list = { "/JettyHandler.py" };
//			for (String file : list)
//			{
//				InputStream in = PythonEngine.class.getResourceAsStream(file);
//				engine.eval(new InputStreamReader(in));
//			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
