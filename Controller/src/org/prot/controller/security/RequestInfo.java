package org.prot.controller.security;

public final class RequestInfo
{
	private final long requestId;

	private String appId;

	private long timestamp;

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
}
