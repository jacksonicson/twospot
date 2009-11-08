package org.prot.appserver.runtime.jython;

import java.io.File;
import java.util.Stack;

import org.prot.appserver.config.Configuration;
import org.python.core.Py;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

public class EnginePool
{
	private Stack<PythonInterpreter> interpreters = new Stack<PythonInterpreter>();

	PySystemState engineSys = new PySystemState();

	public EnginePool()
	{
		Configuration config = Configuration.getInstance();
		
		// Environment
		engineSys.path.append(Py.newString(config.getPythonLibs()));
		engineSys.path.append(Py.newString(config.getDjangoLibs()));
		
		// Server
		engineSys.path.append(Py.newString(new File("/bin").getAbsolutePath()));
		
		// Application
		engineSys.path.append(Py.newString(config.getAppDirectory() + "/WEB-INF/python"));
	}

	public PythonInterpreter acquireInterpreter()
	{
		synchronized (interpreters)
		{
			PythonInterpreter interpreter = interpreters.pop();
			if (interpreter == null)
			{
				Py.setSystemState(engineSys);
				interpreter = new PythonInterpreter();
			}

			return interpreter;
		}
	}

	public void returnInterpreter(PythonInterpreter interpreter)
	{
		synchronized (interpreters)
		{
			interpreters.add(interpreter);
		}
	}
}
