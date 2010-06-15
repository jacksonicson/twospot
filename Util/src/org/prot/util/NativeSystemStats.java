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
package org.prot.util;

public class NativeSystemStats implements ISystemStats {

	@Override
	public long getCpuTotal() {
		return 0;
	}

	@Override
	public long getFreePhysicalMemorySize() {
		return 500;
	}

	@Override
	public long getProcTotal() {
		return 4000;
	}

	@Override
	public double getProcessLoadSinceLastCall() {
		return 0;
	}

	@Override
	public double getSystemIdle() {
		return 0;
	}

	@Override
	public double getSystemLoad() {
		return 0;
	}

	@Override
	public long getTotalPhysicalMemorySize() {
		return 0;
	}
}
