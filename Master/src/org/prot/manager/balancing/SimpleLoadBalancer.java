package org.prot.manager.balancing;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.prot.manager.data.ControllerInfo;
import org.prot.manager.data.ControllerRegistry;

public class SimpleLoadBalancer implements LoadBalancer
{
	private ControllerRegistry registry;

	@Override
	public Set<ControllerInfo> selectController(String appId)
	{
		Set<ControllerInfo> result = new HashSet<ControllerInfo>();

		Collection<ControllerInfo> infos = registry.getControllers();
		for (ControllerInfo info : infos)
		{
			boolean contains = info.getManagementData().getRunningApps().contains(appId);
			if (contains)
			{
				result.add(info);
				return result;
			}
		}

		// TODO: Change this!
		if (infos.isEmpty() == false)
			result.add(infos.iterator().next());

		return result;
	}

	public void setRegistry(ControllerRegistry registry)
	{
		this.registry = registry;
	}
}
