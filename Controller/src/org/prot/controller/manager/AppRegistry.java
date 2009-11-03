package org.prot.controller.manager;

import java.util.Hashtable;
import java.util.Map;
import java.util.Stack;

public class AppRegistry
{
	private final int startPort = 9090;
	private int currentPort = startPort;

	private Stack<Integer> freePorts = new Stack<Integer>();

	private Map<String, AppInfo> appInfos = new Hashtable<String, AppInfo>();

	private int getPort()
	{
		if (freePorts.isEmpty())
		{
			return this.currentPort++;
		}

		return freePorts.peek();
	}

	private void putApp(AppInfo appInfo)
	{
		this.appInfos.put(appInfo.getAppId(), appInfo);
	}

	public boolean hasApp(String appId)
	{
		return appInfos.containsKey(appId);
	}

	public AppInfo getAppInfo(String appId)
	{
		return appInfos.get(appId);
	}

	public AppInfo registerApp(String appId) throws DuplicatedAppException
	{
		AppInfo appInfo = appInfos.get(appId);
		if (appInfo != null)
		{
			throw new DuplicatedAppException(appInfo);
		}

		appInfo = new AppInfo();
		appInfo.setAppId(appId);
		putApp(appInfo);
		appInfo.setPort(getPort());

		return appInfo;
	}
}
