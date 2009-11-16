package org.prot.manager.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControllerRegistry
{
	private Map<String, ControllerInfo> controllers = new HashMap<String, ControllerInfo>();
	
	public synchronized void update(List<ControllerInfo> infos)
	{
		for(ControllerInfo info : infos)
		{
			String address = info.getAddress();
			if(controllers.containsKey(address))
			{
				ControllerInfo toUpdate = controllers.get(address);
				toUpdate.update(info);
			} else
			{
				controllers.put(address, info);
			}
		}
	}
	
	public synchronized void fetchControllerData()
	{
		// TODO!
	}
	
	public Collection<ControllerInfo> getControllers()
	{
		return controllers.values();
	}
	
	public ControllerInfo selectController()
	{
		if(controllers.isEmpty())
			return null; 
		
		ControllerInfo first = controllers.values().iterator().next();
		return first;
	}
}
