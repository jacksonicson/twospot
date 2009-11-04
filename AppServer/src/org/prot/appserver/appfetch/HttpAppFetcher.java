package org.prot.appserver.appfetch;

import java.io.IOException;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.prot.appserver.Configuration;
import org.prot.appserver.app.AppInfo;

public class HttpAppFetcher implements AppFetcher
{
	private HttpClient httpClient;

	private void startHttp() {
		try
		{
			httpClient = new HttpClient();
			httpClient.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
			httpClient.start();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void stopHttp() {
		try
		{
			httpClient.stop();
		} catch (Exception e)
		{
			e.printStackTrace();
		} 
	}
	
	@Override
	public AppInfo fetchApp(String appId)
	{
		AppInfo appInfo = new AppInfo(); 
		
		ContentExchange exchange = new ContentExchange(true);
		exchange.setMethod("GET");
		exchange.setURL("http://localhost:5050/app/" + appId);
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
