package org.prot.controller.manager2;

import java.util.ArrayList;
import java.util.List;

public class AppMonitor extends Thread
{
	private boolean stop = false;

	private List<AppProcess> processList = new ArrayList<AppProcess>();

	public void registerProcess(AppProcess process)
	{
		synchronized (processList)
		{
			this.processList.add(process);
			processList.notify();
		}
	}

	public AppProcess getProcess(AppInfo appInfo)
	{
		synchronized (processList)
		{
			for (AppProcess process : processList)
			{
				if (process.getOwner().equals(appInfo))
					return process;
			}
		}
		return null;
	}

	public void run()
	{
		int index = 0;
		Runnable current = null;

		while (stop == false)
		{
			synchronized (processList)
			{
				try
				{
					while (processList.isEmpty())
						wait();

					if (index > processList.size())
						index = 0;

					current = processList.get(index);
					index++;

				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}

			current.run();
		}
	}

}
