package org.prot.controller.management;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.log4j.Logger;
import org.prot.util.managment.Ping;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

public class ExceptionSafeProxy implements InvocationHandler
{
	private static final Logger logger = Logger.getLogger(ExceptionSafeProxy.class);

	private static final String APP_SERVER_ADDRESS = "localhost";

	private static final int RMI_PORT = 2299;

	private String appId;

	private Object obj;

	private ExceptionSafeProxy(String appId)
	{
		this.appId = appId;
	}

	public static Object newInstance(ClassLoader loader, Class<?> clazz, String appId)
	{
		return Proxy.newProxyInstance(loader, new Class<?>[] { clazz }, new ExceptionSafeProxy(appId));
	}

	private void connect() throws Throwable
	{
		if (obj != null)
			return;

		try
		{
			RmiProxyFactoryBean proxyFactory = new RmiProxyFactoryBean();
			proxyFactory.setServiceInterface(Ping.class);
			proxyFactory
					.setServiceUrl("rmi://" + APP_SERVER_ADDRESS + ":" + RMI_PORT + "/appserver/" + appId);
			proxyFactory.afterPropertiesSet();
			obj = proxyFactory.getObject();
		} catch (Exception e)
		{
			logger.debug("Exception while connecting to AppServer: " + appId);
			logger.trace(e);
			obj = null;
			throw e;
		}
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		connect();

		try
		{
			return method.invoke(obj, args);
		} catch (Exception e)
		{
			obj = null;
			logger.debug("Exception in proxy");
			logger.trace(e);
			throw e;
		}
	}
}
