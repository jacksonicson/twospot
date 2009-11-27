package org.prot.controller.security;

import java.io.IOException;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.server.Request;
import org.prot.controller.handler.ControllerProxy;
import org.prot.controller.manager.AppManager;

public class RequestManager
{
	private static final Logger logger = Logger.getLogger(RequestManager.class);

	private AppManager appManager;

	private ControllerProxy controllerProxy;

	private Timer timer = new Timer();

	private static long requestCounter = 0;

	private Queue<RequestInfo> toStart = new ConcurrentLinkedQueue<RequestInfo>();

	private List<RequestInfo> running = new Vector<RequestInfo>();

	private final long newRequestId()
	{
		return requestCounter++;
	}

	public RequestInfo registerRequest(String appId, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response, HttpURI dest)
	{
		RequestInfo info = new RequestInfo(newRequestId());
		info.setAppId(appId);
		info.setTimestamp(System.currentTimeMillis());
		info.setBaseRequest(baseRequest);
		info.setRequest(request);
		info.setResponse(response);
		info.setDestination(dest);

		toStart.add(info);
		start();

		logger.debug("New request scheduled");
		return info;
	}

	private void start()
	{
		while (toStart.isEmpty() == false)
		{
			RequestInfo info = toStart.poll();
			try
			{
				controllerProxy.forwardRequest(info.getBaseRequest(), info.getRequest(), info.getResponse(),
						info.getDestination(), info);

				running.add(info);

			} catch (Exception e)
			{
				// Inform the client
				logger.error("Unknown error while handling the request", e);
				try
				{
					info.getResponse().sendError(HttpStatus.INTERNAL_SERVER_ERROR_500,
							"Controller could not handle the request");
				} catch (IOException e1)
				{
					logger.error("Error while sending error", e1);
				}

				info.getBaseRequest().setHandled(true);
			}
		}
	}

	public void requestFinished(RequestInfo info)
	{
		logger.debug("Removing request: " + info.getRequestId());
		running.remove(info);
	}

	public RequestManager()
	{
		timer.scheduleAtFixedRate(new CheckRequestsTask(), 0, 1000);
	}

	class CheckRequestsTask extends TimerTask
	{
		@Override
		public void run()
		{
			try
			{
				long currentTime = System.currentTimeMillis();
				for (RequestInfo test : running)
				{
					// Test timestamps
					if ((currentTime - test.getTimestamp()) > 2000)
					{
						logger.info("killing appserver");
						
						if(test.getExchange() != null)
							test.getExchange().cancel();
						
						appManager.killApp(test.getAppId());
						running.remove(test);
						break;
					}
				}
			} catch (Exception e)
			{
				logger.error("Error while killing an AppServer", e);
			}
		}
	}

	public void setAppManager(AppManager appManager)
	{
		this.appManager = appManager;
	}

	public void setControllerProxy(ControllerProxy controllerProxy)
	{
		this.controllerProxy = controllerProxy;
	}
}
