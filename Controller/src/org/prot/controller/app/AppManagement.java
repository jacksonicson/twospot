package org.prot.controller.app;

import org.prot.util.managment.gen.ManagementData;

public class AppManagement
{
	private ManagementData.AppServer appServer;

	public void update(ManagementData.AppServer appServer)
	{
		this.appServer = appServer;
	}

	public ManagementData.AppServer getAppServer()
	{
		return this.appServer;
	}
}
