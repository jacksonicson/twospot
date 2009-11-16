package org.prot.manager.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControllerRegistry
{
	private Map<String, ControllerInfo> controllers = new HashMap<String, ControllerInfo>();
	
	public void update(List<ControllerInfo> infos)
	{
		System.out.println("Update: " + infos.size());
	}
}
