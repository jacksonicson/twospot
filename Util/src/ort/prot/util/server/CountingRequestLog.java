package ort.prot.util.server;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

/**
 * 
 * @author Andreas Wolke
 * 
 */
public class CountingRequestLog extends AbstractLifeCycle implements RequestLog {
	private static final Logger logger = Logger.getLogger(CountingRequestLog.class);

	private long counter = 0;

	private long summedRequestTime;

	@Override
	public void log(Request request, Response response) {
		this.counter++;
		this.summedRequestTime += System.currentTimeMillis() - request.getTimeStamp();
	}

	public void reset() {
		this.counter = 0;
		this.summedRequestTime = 0;
	}

	public long getCounter() {
		return this.counter;
	}

	public long getSummedRequestTime() {
		return this.summedRequestTime;
	}
}
