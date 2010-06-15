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
package org.prot.util.scheduler;

import java.util.List;
import java.util.Timer;

/**
 * 
 * @author Andreas Wolke
 * 
 */
public final class Scheduler {
	private static final Timer timer = new Timer(true);

	public static void addTask(SchedulerTask task) {
		timer.scheduleAtFixedRate(task, 0, task.getInterval());
	}

	public static void setTasks(List<SchedulerTask> tasks) {
		for (SchedulerTask task : tasks)
			addTask(task);
	}

	public static void removeTask(SchedulerTask task) {
		task.cancel();
		timer.purge();
	}
}
