package org.prot.util.managment;

import java.util.concurrent.atomic.AtomicLong;

public final class RequestStats
{
	private static AtomicLong requests = new AtomicLong();

	public static final void countRequest()
	{
		requests.incrementAndGet();
		requests.compareAndSet(1000, 0);
	}

	public static final long getRps()
	{
		return requests.get();
	}
}
