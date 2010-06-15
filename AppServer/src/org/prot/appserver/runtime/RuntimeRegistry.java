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
package org.prot.appserver.runtime;

import java.util.List;

public class RuntimeRegistry
{
	private List<AppRuntime> runtimes = null;

	public AppRuntime getRuntime(String runtimeIdentifier) throws NoSuchRuntimeException
	{
		for(AppRuntime runtime : runtimes) {
			if(runtime.getIdentifier().equalsIgnoreCase(runtimeIdentifier))
				return runtime;
		}
		
		throw new NoSuchRuntimeException(); 
	}

	public void setRuntimes(List<AppRuntime> runtimes)
	{
		this.runtimes = runtimes;
	}
}
