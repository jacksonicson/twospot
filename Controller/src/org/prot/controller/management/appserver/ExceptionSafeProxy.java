package org.prot.controller.management.appserver;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.log4j.Logger;
import org.prot.appserver.management.IAppServerStats;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

public class ExceptionSafeProxy implements InvocationHandler
{
	private static final Logger logger = Logger.getLogger(ExceptionSafeProxy.class);

	private static final String APP_SERVER_ADDRESS = "localhost";

	private String appId;

	private Object obj;

	public ExceptionSafeProxy(String appId)
	{
		this.appId = appId;
	}

	public static Object newInstance(ClassLoader loader, Class<?> clazz, String appId)
	{
		return Proxy.newProxyInstance(loader, new Class<?>[] { clazz }, new ExceptionSafeProxy(appId));
	}

	private static final int getRmiPort()
	{
		return 2299;
	}

	private void connect()
	{
		if (obj != null)
			return;

		try
		{
			RmiProxyFactoryBean proxyFactory = new RmiProxyFactoryBean();
			proxyFactory.setServiceInterface(IAppServerStats.class);
			proxyFactory.setServiceUrl("rmi://" + APP_SERVER_ADDRESS + ":" + getRmiPort() + "/appserver/"
					+ appId);
			proxyFactory.afterPropertiesSet();

			obj = proxyFactory.getObject();

		} catch (Exception e)
		{
			logger.debug("exception while connecting to AppServer: " + appId);
			logger.trace(e);
			obj = null;
		}
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		connect();

		Object result = null;

		try
		{
			result = method.invoke(obj, args);
		} catch (Exception e)
		{
			obj = null;
			logger.debug("exception in proxy");
			logger.trace(e);
		}

		return result;
	}
}
