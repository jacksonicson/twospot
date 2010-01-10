package org.prot.frontend.handlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.prot.frontend.cache.CacheResult;

public class RequestState
{
	private final String appId;
	private final Request baseRequest;
	private final HttpServletRequest request;
	private final HttpServletResponse response;

	private CacheResult cached;

	public RequestState(String appId, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response)
	{
		this.appId = appId;
		this.baseRequest = baseRequest;
		this.request = request;
		this.response = response;
	}

	public void setCached(CacheResult cached)
	{
		this.cached = cached;
	}

	public CacheResult getCached()
	{
		return cached;
	}

	public String getAppId()
	{
		return appId;
	}

	public Request getBaseRequest()
	{
		return baseRequest;
	}

	public HttpServletRequest getRequest()
	{
		return request;
	}

	public HttpServletResponse getResponse()
	{
		return response;
	}
}
