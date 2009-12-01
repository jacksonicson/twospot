package org.prot.controller.management;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.log4j.Logger;
import org.prot.controller.manager.appserver.IAppServerStats;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

public class ExceptionSafeProxy implements InvocationHandler
{
	private static final Logger logger = Logger.getLogger(ExceptionSafeProxy.class);

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

	private void connect()
	{
		if (obj != null)
			return;

		try
		{
			RmiProxyFactoryBean proxyFactory = new RmiProxyFactoryBean();
			proxyFactory.setServiceInterface(IAppServerStats.class);
			proxyFactory.setServiceUrl("rmi://" + "localhost:2299" + "/appserver/" + appId);
			proxyFactory.afterPropertiesSet();

			obj = proxyFactory.getObject();

		} catch (Exception e)
		{
			logger.debug("exception while connecting to AppServer: " + appId, e);
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
			logger.debug("exception in proxy", e);
		}

		return result;
	}
}
