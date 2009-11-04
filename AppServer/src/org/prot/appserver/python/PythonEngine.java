package org.prot.appserver.python;

import java.io.InputStream;
import java.io.InputStreamReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.python.core.Py;
import org.python.core.PySystemState;

public class PythonEngine
{
	private ScriptEngine engine;
	private Invocable invocable; 

	public Invocable getInvocable() {
		return this.invocable; 
	}
	
	public ScriptEngine getEngine() {
		return this.engine; 
	}
	
	public void start()
	{
		ScriptEngineManager engineManager = new ScriptEngineManager();
		engineManager.put("python.home", "C:/jython2.5.1/");

		PySystemState engineSys = new PySystemState();
		engineSys.path.append(Py.newString("C:/jython2.5.1/Lib"));
		engineSys.path.append(Py.newString("C:/jython2.5.1/Lib/site-packages"));
		engineSys.path.append(Py.newString("D:/work/jython/myproject"));
		Py.setSystemState(engineSys);

		System.out.println("starting jython...");
		engine = engineManager.getEngineByName("jython");
		invocable = (Invocable)engine; 
		
		try
		{
			String[] scripts = { "/JettyHandler.py", "/Request.py" };

			for (String script : scripts)
			{
				InputStream in = PythonEngine.class.getResourceAsStream(script);
				engine.eval(new InputStreamReader(in));
			}
			
			invocable.invokeFunction("sayHello");
			
		} catch (ScriptException e)
		{
			e.printStackTrace();
		} catch (NoSuchMethodException e)
		{
			e.printStackTrace();
		}
	}
}
