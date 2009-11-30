package org.prot.controller.management;

import java.util.List;

public class Resources implements IResources
{
	private static final String NAME = "Resources";

	@Override
	public long loadAverage()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long requestsPerMinute()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long runningAppServers()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	public List<String> getApps()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
