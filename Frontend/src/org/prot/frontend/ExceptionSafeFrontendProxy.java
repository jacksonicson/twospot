package org.prot.frontend;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.log4j.Logger;
import org.prot.frontend.config.Configuration;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

public class ExceptionSafeFrontendProxy implements InvocationHandler
{
	private static final Logger logger = Logger.getLogger(ExceptionSafeFrontendProxy.class);

	private Class<?> clazz;

	private Object obj;

	public ExceptionSafeFrontendProxy(Class<?> clazz)
	{
		this.clazz = clazz;
	}

	public static Object newInstance(ClassLoader loader, Class<?> clazz)
	{
		return Proxy
				.newProxyInstance(loader, new Class<?>[] { clazz }, new ExceptionSafeFrontendProxy(clazz));
	}

	private void connect()
	{
		if (obj != null)
			return;

		if (Configuration.getConfiguration().getManagerAddress() == null)
		{
			logger.warn("No manager address");
			return;
		}

		if (obj == null)
		{
			try
			{
				RmiProxyFactoryBean proxyFactory = new RmiProxyFactoryBean();

				logger.debug("Connecting with master: "
						+ Configuration.getConfiguration().getManagerAddress());

				proxyFactory.setServiceInterface(clazz);
				proxyFactory.setServiceUrl("rmi://" + Configuration.getConfiguration().getManagerAddress()
						+ ":" + Configuration.getConfiguration().getMasterRmiPort() + "/frontendService");

				proxyFactory.afterPropertiesSet();

				obj = proxyFactory.getObject();
			} catch (Exception e)
			{
				// Connection failed
				logger.info("Connection with master failed");
				obj = null;
			}
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
			logger.debug("exception in proxy - connection with service failed");
			logger.trace(e);
		}

		return result;
	}
}
