package ort.prot.util.server;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

public class CountingRequestLog extends AbstractLifeCycle implements RequestLog
{
	private static final Logger logger = Logger.getLogger(CountingRequestLog.class);

	private long counter = 0;

	public CountingRequestLog()
	{
		// logger.error("asdlöfjasdöfj");
	}

	@Override
	public void log(Request request, Response response)
	{
		this.counter++;
		// System.out.println("time: " + (System.currentTimeMillis() -
		// request.getTimeStamp()));
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
