package org.prot.app.services.security;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

public class DosPrevention
{
	private static final Logger logger = Logger.getLogger(DosPrevention.class);

	private static final long MAX_REQUEST_TIME = 3000; 
	
	private static long requestIdCounter = 0;

	private Map<Long, Long> requests = new ConcurrentHashMap<Long, Long>();

	public DosPrevention()
	{
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new Tick(), 0, 1000);
	}
	
	public final long startRequest()
	{
		long requestId = requestIdCounter++;
		requests.put(requestId, System.currentTimeMillis());
		return requestId;
	}

	public final void finishRequest(long requestId)
	{
		requests.remove(requestId);
	}

	private final void checkRequests()
	{
		long current = System.currentTimeMillis(); 
		for(long requestId : requests.keySet())
		{
			// Could be null in the meantime (Multithreading)
			Long time = requests.get(requestId);
			if(time == null)
				continue;
			
			if((current - time) > MAX_REQUEST_TIME)
			{
				logger.fatal("Possible DOS attack detected - shutting down"); 
				System.exit(2); 
			}
		}
	}
	
	private final void checkMemory()
	{
		// TODO
	}
	
	class Tick extends TimerTask
	{
		@Override
		public void run()
		{
			checkRequests();
			checkMemory();
		}
	}
}
