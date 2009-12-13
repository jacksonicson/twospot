package org.prot.controller.handler;

import org.prot.util.handler.HttpProxyHelper;

public class ControllerProxy extends HttpProxyHelper<String>
{
	private RequestManager requestManager;

	@Override
	protected boolean error(String appId, Throwable t)
	{
		return requestManager.requestError(appId, t);
	}

	public void setRequestManager(RequestManager requestManager)
	{
		this.requestManager = requestManager;
	}
}
