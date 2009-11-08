package org.prot.appserver.runtime;

import java.util.List;

public class RuntimeRegistry
{
	private List<AppRuntime> runtimes = null;

	public AppRuntime getRuntime(String runtimeIdentifier) throws NoSuchRuntimeException
	{
		for(AppRuntime runtime : runtimes) {
			if(runtime.getIdentifier().equalsIgnoreCase(runtimeIdentifier))
				return runtime;
		}
		
		throw new NoSuchRuntimeException(); 
	}

	public void setRuntimes(List<AppRuntime> runtimes)
	{
		this.runtimes = runtimes;
	}
}
