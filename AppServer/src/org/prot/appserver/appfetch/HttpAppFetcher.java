package org.prot.appserver.appfetch;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.http.HttpStatus;
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
			System.exit(1);
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
			System.exit(1);
		}
	}

	@Override
	public AppInfo fetchApp(String appId)
	{
		AppInfo appInfo = new AppInfo();

		ContentExchange exchange = new ContentExchange(true)
		{
			protected void onConnectionFailed(Throwable x)
			{
				super.onConnectionFailed(x);
				logger.error("Connection failed while downloading WAR archive", x);
				System.exit(1);
			}

			protected void onException(Throwable x)
			{
				super.onException(x);
				logger.error("Error while downloading WAR archive", x);
				System.exit(1);
			}
		};

		exchange.setMethod("GET");
		exchange.setURL("http://localhost:5050/" + appId); // TODO: Not static

		try
		{
			startHttp();

			httpClient.send(exchange);
			exchange.waitForDone();

			// Check status
			int status = exchange.getStatus();
			if (status != HttpStatus.OK_200)
				return null;

			// Check size (applications cannot be 0 bytes)
			if (exchange.getResponseContentBytes().length == 0)
				return null;

			appInfo.setWarFile(exchange.getResponseContentBytes());
			appInfo.setAppId(Configuration.getInstance().getAppId());

			stopHttp();

		} catch (InterruptedException e)
		{
			logger.error("Interrupted while downloading WAR archive", e);
			System.exit(1);
		} catch (IOException e)
		{
			logger.error("IOException while downloading WAR archive", e);
			System.exit(1);
		}

		return appInfo;
	}
}
