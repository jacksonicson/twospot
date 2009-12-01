package org.prot.manager.watcher;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;

import org.apache.log4j.Logger;
import org.springframework.jmx.access.MBeanProxyFactoryBean;
import org.springframework.jmx.support.MBeanServerConnectionFactoryBean;

public class ExceptionSafeProxy implements InvocationHandler
{
	private static final Logger logger = Logger.getLogger(ExceptionSafeProxy.class);

	private Class clazz;

	private String address;

	private String objName;

	private Object obj;

	public ExceptionSafeProxy(Class clazz, String address, String objName)
	{
		this.clazz = clazz;
		this.address = address;
		this.objName = objName;
	}

	public static Object newInstance(ClassLoader loader, Class<?> clazz, String address, String objName)
	{
		return Proxy.newProxyInstance(loader, new Class<?>[] { clazz }, new ExceptionSafeProxy(clazz,
				address, objName));
	}

	private void connect()
	{
		if (obj != null)
			return;

		MBeanServerConnectionFactoryBean connection = new MBeanServerConnectionFactoryBean();
		// TODO: not static
		try
		{
			connection.setServiceUrl("service:jmx:rmi:///jndi/rmi://" + address + ":2299/server");
			connection.afterPropertiesSet();

			MBeanProxyFactoryBean proxy = new MBeanProxyFactoryBean();
			proxy.setObjectName(objName);
			proxy.setProxyInterface(clazz);
			proxy.setServer((MBeanServerConnection) connection.getObject());
			proxy.afterPropertiesSet();
			obj = proxy.getObject();

		} catch (MalformedURLException e)
		{
			logger.debug("could not connect with controller", e);
		} catch (IOException e)
		{
			logger.debug("could not connect with controller", e);
		} catch (MalformedObjectNameException e)
		{
			logger.debug("could not connect with controller", e);
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
