package org.prot.controller.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.server.Request;

public final class RequestInfo
{
	private final long requestId;

	private String appId;

	private long timestamp;

	private HttpURI destination;

	private Request baseRequest;

	private HttpServletRequest request;

	private HttpServletResponse response;

	private HttpExchange exchange;

	public RequestInfo(long requestId)
	{
		this.requestId = requestId;
	}

	public String getAppId()
	{
		return appId;
	}

	public void setAppId(String appId)
	{
		this.appId = appId;
	}

	public long getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}

	public long getRequestId()
	{
		return requestId;
	}

	public HttpServletResponse getResponse()
	{
		return response;
	}

	public void setResponse(HttpServletResponse response)
	{
		this.response = response;
	}

	public HttpExchange getExchange()
	{
		return exchange;
	}

	public void setExchange(HttpExchange exchange)
	{
		this.exchange = exchange;
	}

	public Request getBaseRequest()
	{
		return baseRequest;
	}

	public void setBaseRequest(Request baseRequest)
	{
		this.baseRequest = baseRequest;
	}

	public HttpServletRequest getRequest()
	{
		return request;
	}

	public void setRequest(HttpServletRequest request)
	{
		this.request = request;
	}

	public HttpURI getDestination()
	{
		return destination;
	}

	public void setDestination(HttpURI destination)
	{
		this.destination = destination;
	}
}
