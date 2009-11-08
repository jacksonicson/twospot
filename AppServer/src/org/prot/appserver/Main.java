package org.prot.appserver;

import org.apache.log4j.xml.DOMConfigurator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.component.LifeCycle.Listener;
import org.prot.appserver.config.Configuration;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

public class Main implements Listener
{
	private void startJava() throws Exception
	{
		XmlBeanFactory factory = new XmlBeanFactory(new ClassPathResource("/etc/spring/spring_java.xml",
				getClass()));

		// Configure server port
		SelectChannelConnector connector = (SelectChannelConnector) factory.getBean("Connector");
		connector.setPort(Configuration.getInstance().getAppServerPort());

		// Deployer
		AppDeployer deployer = (AppDeployer) factory.getBean("AppDeployer");

		// Start server
		Server server = (Server) factory.getBean("Server");
		server.addBean(deployer);
		server.addLifeCycleListener(this);
		server.start();
	}

	private void startPython() throws Exception
	{
		XmlBeanFactory factory = new XmlBeanFactory(new ClassPathResource("/etc/spring/spring_python.xml",
				getClass()));

		// Configure server port
		SelectChannelConnector connector = (SelectChannelConnector) factory.getBean("Connector");
		connector.setPort(Configuration.getInstance().getAppServerPort());

		// Start server
		Server server = (Server) factory.getBean("Server");
		server.addLifeCycleListener(this);
		server.start();
	}

	public Main()
	{
		// Configure logger
		DOMConfigurator.configure(Main.class.getResource("/etc/log4j.xml"));
		
		// Create beans
		XmlBeanFactory factory = new XmlBeanFactory(new ClassPathResource("/etc/spring.xml",
				getClass()));
		
		factory.getBean("Lifecycle");
		
//		
//		
//
//		AppFetcher fetcher = (AppFetcher) factory.getBean("AppFetcher");
//
//		AppInfo appInfo = fetcher.fetchApp(Configuration.getInstance().getAppId());
//		Configuration.getInstance().setAppInfo(appInfo);
//
//		WarLoader loader = new WarLoader();
//		loader.handle(appInfo);
//
//		try
//		{
//			switch (appInfo.getRuntime())
//			{
//			case JAVA:
//				startJava();
//				break;
//			case PYTHON:
//				startPython();
//			}
//		} catch (Exception e)
//		{
//		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// Parse command line arguments
		ArgumentParser.parseArguments(args);

		// Launch
		new Main();
	}

	@Override
	public void lifeCycleFailure(LifeCycle arg0, Throwable arg1)
	{
	}

	@Override
	public void lifeCycleStarted(LifeCycle arg0)
	{
		System.out.println("server started");
	}

	@Override
	public void lifeCycleStarting(LifeCycle arg0)
	{
	}

	@Override
	public void lifeCycleStopped(LifeCycle arg0)
	{
	}

	@Override
	public void lifeCycleStopping(LifeCycle arg0)
	{
	}
}
