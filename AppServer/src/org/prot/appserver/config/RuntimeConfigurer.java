package org.prot.appserver.config;

import java.util.Map;

import org.prot.appserver.app.AppInfo;

public interface RuntimeConfigurer
{
	public void configure(AppInfo appInfo, Map yamlObj);
}
