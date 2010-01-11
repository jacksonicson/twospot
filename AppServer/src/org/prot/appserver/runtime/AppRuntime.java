package org.prot.appserver.runtime;

import java.util.Map;

import org.prot.appserver.app.AppInfo;
import org.prot.appserver.management.RuntimeManagement;

public interface AppRuntime
{
	public String getIdentifier();

	public void loadConfiguration(AppInfo appInfo, Map<?, ?> yaml);

	public void launch(AppInfo appInfo) throws Exception;

	public RuntimeManagement getManagement();
}
