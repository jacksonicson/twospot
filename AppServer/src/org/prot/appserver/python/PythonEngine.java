package org.prot.appserver.python;

import java.io.InputStream;
import java.io.InputStreamReader;

import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

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
		ScriptEngineManager engineManager = new ScriptEngineManager();

		PySystemState engineSys = new PySystemState();
		engineSys.path.append(Py.newString("C:/jython2.5.1/Lib"));
		engineSys.path.append(Py.newString("C:/jython2.5.1/Lib/site-packages"));
		engineSys.path.append(Py.newString("D:/work/django"));
		Py.setSystemState(engineSys);

		engineManager.put("DJANGO_SETTINGS_MODULE", "blub.settings");
		engineManager.put("python.home", "C:/jython2.5.1/");
		engine = engineManager.getEngineByName("jython");

		ScriptContext context = engine.getContext();
		context.setAttribute("python.home", "C:/jython2.5.1/", ScriptContext.GLOBAL_SCOPE);
		context.setAttribute("runserver", "", ScriptContext.ENGINE_SCOPE);
		
		context.setAttribute("DJANGO_SETTINGS_MODULE", "blub.settings", ScriptContext.GLOBAL_SCOPE);
		context.setAttribute("DJANGO_SETTINGS_MODULE", "blub.settings", ScriptContext.ENGINE_SCOPE);
		
		
		System.setProperty("python.home", "C:/jython2.5.1/");
		System.setProperty("DJANGO_SETTINGS_MODULE", "blub.settings");

		try
		{

//			List<String> params = new ArrayList<String>();
//			params.add("runserver");
//			
//			FileReader reader = new FileReader("D:/work/django/blub/manage.py");
//
//			engine.eval("import sys");
//			engine.eval("sys.argv.append('runserver')");
//
//			engine.eval(reader);
			
			String[] list = {"/Request.py", "/JettyHandler.py"};
			for(String file : list) {
				InputStream in = PythonEngine.class.getResourceAsStream(file); 
				engine.eval(new InputStreamReader(in)); 
			}
			
			System.out.println("done)");

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
