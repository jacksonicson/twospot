package org.prot.appserver.appfetch;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.prot.appserver.app.AppInfo;
import org.prot.appserver.config.Configuration;

public class HttpAppFetcher implements AppFetcher
{
	private static final Logger logger = Logger.getLogger(HttpAppFetcher.class);
	
	private HttpClient httpClient;

	private void startHttp()
	{
		try
		{
			httpClient = new HttpClient();
			httpClient.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
			httpClient.start();
		} catch (Exception e)
		{
			logger.error("Could not start the http client", e); 
		}
	}

	private void stopHttp()
	{
		try
		{
			httpClient.stop();
		} catch (Exception e)
		{
			logger.error("Could not stop the http client", e); 
		}
	}

	@Override
	public AppInfo fetchApp(String appId)
	{
		AppInfo appInfo = new AppInfo();

		ContentExchange exchange = new ContentExchange(true);
		exchange.setMethod("GET");
		exchange.setURL("http://localhost:5050/" + appId); // TODO: Not static
															
		try
		{
			startHttp();

			httpClient.send(exchange);
			exchange.waitForDone();

			appInfo.setWarFile(exchange.getResponseContentBytes());
			appInfo.setAppId(Configuration.getInstance().getAppId());

			stopHttp();

		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		return appInfo;
	}
}
