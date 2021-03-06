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
package org.prot.appserver.runtime.java;

import org.eclipse.jetty.server.Connector;
import org.prot.appserver.management.RuntimeManagement;
import org.prot.util.ISystemStats;
import org.prot.util.NativeSystemStats;
import org.prot.util.managment.gen.ManagementData.AppServer;

import ort.prot.util.server.CountingRequestLog;

public class JettyAppManagement implements RuntimeManagement {
	private long startTime = System.currentTimeMillis();

	private long lastPoll = 0;

	private ISystemStats systemStats = new NativeSystemStats();

	private CountingRequestLog countingRequestLog;

	private Connector connector;

	private long getRuntime() {
		return System.currentTimeMillis() - startTime;
	}

	private float getRps() {
		long time = System.currentTimeMillis() - lastPoll;
		double rps = (double) countingRequestLog.getCounter() / (double) (time / 1000 + 1);
		return (float) rps;
	}

	private float getDelay() {
		double delay = (double) countingRequestLog.getSummedRequestTime()
				/ ((double) countingRequestLog.getCounter() + 1d);
		return (float) delay;
	}

	private boolean isLowResources() {
		return connector.isLowResources();
	}

	@Override
	public void fill(AppServer.Builder appServer) {
		appServer.setRuntime(getRuntime());
		appServer.setProcCpu(systemStats.getProcessLoadSinceLastCall());
		appServer.setCpuTotal(systemStats.getCpuTotal());
		appServer.setCpuProcTotal(systemStats.getProcTotal());
		appServer.setRps(getRps());
		appServer.setAverageDelay(getDelay());
		appServer.setOverloaded(isLowResources());

		lastPoll = System.currentTimeMillis();
		countingRequestLog.reset();
	}

	public void setCountingRequestLog(CountingRequestLog countingRequestLog) {
		this.countingRequestLog = countingRequestLog;
	}

	public void setConnector(Connector connector) {
		this.connector = connector;
	}
}
