package org.prot.manager.watcher;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ConnectException;
import java.net.MalformedURLException;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;

import org.apache.log4j.Logger;
import org.prot.manager.config.Configuration;
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

	private MBeanServerConnectionFactoryBean connection;
	private MBeanProxyFactoryBean proxy;

	private void disconnect()
	{
		proxy.destroy();
		try
		{
			connection.destroy();
		} catch (IOException e)
		{
			logger.debug("Disconnect failed");
			logger.trace(e);
		}
		proxy = null;
		connection = null;
	}

	private static final int getControllerRmiPort()
	{
		return Configuration.getConfiguration().getRmiControllerPort();
	}

	private void connect()
	{
		if (obj != null)
			return;

		connection = new MBeanServerConnectionFactoryBean();
		// TODO: not static
		try
		{
			connection.setServiceUrl("service:jmx:rmi:///jndi/rmi://" + address + ":"
					+ getControllerRmiPort() + "/controller");
			connection.afterPropertiesSet();

			proxy = new MBeanProxyFactoryBean();
			proxy.setObjectName(objName);
			proxy.setProxyInterface(clazz);
			proxy.setServer((MBeanServerConnection) connection.getObject());
			proxy.afterPropertiesSet();
			obj = proxy.getObject();

		} catch (MalformedURLException e)
		{
			logger.debug("could not connect with controller");
			logger.trace(e);
			disconnect();
		} catch (IOException e)
		{
			logger.debug("could not connect with controller");
			logger.trace(e);
			disconnect();
		} catch (MalformedObjectNameException e)
		{
			logger.debug("could not connect with controller");
			logger.trace(e);
			disconnect();
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
			disconnect();
			logger.debug("exception in proxy - connection with service failed");
			logger.trace(e);
			throw new ConnectException();
		}

		return result;
	}
}
