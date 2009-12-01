package org.prot.util.scheduler;

import java.util.TimerTask;

public abstract class SchedulerTask extends TimerTask
{
	public abstract long getInterval();

	public abstract void run();
}
