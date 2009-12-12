package ort.prot.util.server;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

public class CountingRequestLog extends AbstractLifeCycle implements RequestLog
{
	private long counter = 0;

	@Override
	public void log(Request request, Response response)
	{
		this.counter++;
	}

	public void reset()
	{
		this.counter = 0;
	}

	public long getCounter()
	{
		return this.counter;
	}
}
