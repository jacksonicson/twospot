/*******************************************************************************
 * Copyright (c) 2010 Andreas Wolke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Wolke - initial API and implementation and initial documentation
 *******************************************************************************/
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
