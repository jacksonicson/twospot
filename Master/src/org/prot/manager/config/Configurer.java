package org.prot.manager.config;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

public class Configurer extends PropertyPlaceholderConfigurer
{
	public Configurer()
	{
		super.setProperties(Configuration.getConfiguration().getProperties());
	}
}
