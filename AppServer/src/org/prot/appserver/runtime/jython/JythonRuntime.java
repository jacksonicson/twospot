package org.prot.appserver.runtime.jython;

import java.util.Map;

import org.prot.appserver.app.AppInfo;
import org.prot.appserver.runtime.AppRuntime;

public class JythonRuntime implements AppRuntime
{
	private static final String IDENTIFIER = "PYTHON";
	
	@Override
	public String getIdentifier()
	{
		return IDENTIFIER; 
	}

	@Override
	public void launch()
	{
		System.out.println("Launching python runtime"); 
	}

	@Override
	public void loadConfiguration(AppInfo appInfo, Map yaml)
	{
		System.out.println("Loading python configuration"); 
	}
}
