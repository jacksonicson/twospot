package org.prot.controller.handler;

import java.net.ConnectException;

import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpExchange;
import org.prot.controller.manager.AppManager;
import org.prot.controller.security.RequestInfo;
import org.prot.controller.security.RequestManager;
import org.prot.util.handler.HttpProxyHelper;

public class ControllerProxy extends HttpProxyHelper<RequestInfo>
{
	private static final Logger logger = Logger.getLogger(ControllerProxy.class);

	private RequestManager requestManager;

	private AppManager appManager;

	@Override
	protected void requestFinished(RequestInfo management)
	{
		requestManager.requestFinished(management);
	}

	@Override
	protected void sentRequest(RequestInfo management, HttpExchange exchange)
	{
		management.setExchange(exchange);
	}

	@Override
	protected boolean error(RequestInfo management, Throwable t)
	{
		requestManager.requestFinished(management);

		if (t instanceof ConnectException)
		{
			String appId = management.getAppId();
			logger.debug("Reporting stale AppServer for AppId: " + appId);

			appManager.reportStaleApp(appId);
			return true;
		}

		return false;
	}

	public void setAppManager(AppManager appManager)
	{
		this.appManager = appManager;
	}

	public void setRequestManager(RequestManager requestManager)
	{
		this.requestManager = requestManager;
	}
}
