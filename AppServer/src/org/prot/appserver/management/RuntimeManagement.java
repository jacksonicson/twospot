package org.prot.appserver.management;

import org.prot.util.managment.gen.ManagementData.AppServer;

public interface RuntimeManagement
{
	public void fill(AppServer.Builder appServer);
}
