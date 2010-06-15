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
package org.prot.controller.app;

public class AppProcess
{
	private transient Process process;
	
	public Process getProcess()
	{
		return this.process; 
	}
	
	public void setProcess(Process process)
	{
		this.process = process;
	}
}
