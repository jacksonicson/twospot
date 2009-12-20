package org.prot.frontend.handlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

public class RequestState
{
	private final String appId;
	private final Request baseRequest;
	private final HttpServletRequest request;
	private final HttpServletResponse response;

	private static final int MAX_CONTROLLERS = 3;
	private int index = 0;
	private String[] controllers = new String[MAX_CONTROLLERS];

	public RequestState(String appId, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response)
	{
		this.appId = appId;
		this.baseRequest = baseRequest;
		this.request = request;
		this.response = response;
	}

	public void useController(String address)
	{
		controllers[index++] = address;
	}

	public boolean isFull()
	{
		return index >= MAX_CONTROLLERS;
	}

	public String[] getControllers()
	{
		return controllers;
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
