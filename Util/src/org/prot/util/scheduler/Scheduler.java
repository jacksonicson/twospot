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
