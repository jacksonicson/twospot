package org.prot.controller.handler;

import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpExchange;
import org.prot.controller.security.RequestInfo;
import org.prot.controller.security.RequestManager;
import org.prot.util.handler.HttpProxyHelper;

public class ControllerProxy extends HttpProxyHelper<RequestInfo>
{
	private static final Logger logger = Logger.getLogger(ControllerProxy.class);

	private RequestManager requestManager;

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
		return requestManager.requestError(management, t);
	}

	public void setRequestManager(RequestManager requestManager)
	{
		this.requestManager = requestManager;
	}
}
