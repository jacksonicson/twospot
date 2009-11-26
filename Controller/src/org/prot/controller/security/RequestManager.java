package org.prot.controller.security;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.prot.controller.manager.AppManager;

public class RequestManager
{
	private AppManager appManager;

	private Timer timer = new Timer();

	private long counter = 0;

	private Map<Long, RequestInfo> requests = new HashMap<Long, RequestInfo>(10);

	public long registerRequest(String appId)
	{
		counter++;

		RequestInfo info = new RequestInfo(counter);
		info.setAppId(appId);
		info.setTimestamp(System.currentTimeMillis());

		synchronized (requests)
		{
			requests.put(counter, info);
		}

		return counter;
	}

	public void removeRequest(long requestId)
	{
		synchronized (requests)
		{
			requests.remove(requestId);
		}
	}

	public RequestManager()
	{
		timer.scheduleAtFixedRate(new CheckRequests(), 0, 1000);
	}

	class CheckRequests extends TimerTask
	{
		@Override
		public void run()
		{
			long currentTime = System.currentTimeMillis();

			Set<Long> toKill = new HashSet<Long>();

			for (Long requestId : requests.keySet())
			{
				RequestInfo info = requests.get(requestId);
				if ((currentTime - info.getTimestamp()) > 10000) // Give it some
																	// seconds
				{
					toKill.add(requestId);
				}
			}

			for (Long killId : toKill)
			{
				System.out.println("Killing request: " + killId);

				RequestInfo info = requests.get(killId);
				String appId = info.getAppId();

				appManager.killApp(appId);

				requests.remove(killId);
			}
		}
	}

	public void setAppManager(AppManager appManager)
	{
		this.appManager = appManager;
	}
}
