package org.prot.appserver.runtime.jython;

import java.util.HashSet;
import java.util.Set;

import org.prot.appserver.app.RuntimeConfiguration;

public class PythonConfiguration implements RuntimeConfiguration
{
	private Set<WebConfiguration> webConfigs = new HashSet<WebConfiguration>();

	public void addWebConfig(WebConfiguration config)
	{
		this.webConfigs.add(config);
	}

	public Set<WebConfiguration> getWebConfigs()
	{
		return webConfigs;
	}

	public void setWebConfigs(Set<WebConfiguration> webConfigs)
	{
		this.webConfigs = webConfigs;
	}
}
