package org.prot.controller;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpFields.Field;
import org.eclipse.jetty.io.EofException;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.prot.controller.manager.AppInfo;
import org.prot.controller.manager.AppManager;
import org.prot.controller.manager.DuplicatedAppException;

public class RequestHandler extends AbstractHandler
{

	private HttpClient httpClient;

	private AppManager appManager;

	public RequestHandler()
	{
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

	public void setAppManager(AppManager appManager)
	{
		this.appManager = appManager;
	}

	private void appServerGoneStale(AppInfo appInfo) throws StaleAppServerException
	{
		appInfo.setStale(true); 
		throw new StaleAppServerException(); 
	}

	private void forwardRequest(AppInfo appInfo, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws StaleAppServerException
	{

		// create request
		int port = appInfo.getPort();
		String url = "http://127.0.0.1:" + port + baseRequest.getUri();
//		System.out.println("url: " + url);

		ContentExchange exchange = new ContentExchange(true);
		exchange.setMethod(baseRequest.getMethod());
		exchange.setURL(url);
		exchange.setVersion(baseRequest.getProtocol());

		Enumeration<String> headerNames = baseRequest.getHeaderNames();
		while (headerNames.hasMoreElements())
		{
			String headerName = headerNames.nextElement();

			Enumeration<String> headerValues = baseRequest.getHeaders(headerName);
			while (headerValues.hasMoreElements())
			{
				String headerValue = headerValues.nextElement();

				// System.out.println("request header: " + headerName + ":"
				// + headerValue);
				exchange.addRequestHeader(headerName, headerValue);
			}
		}

		// TODO: for testing purpose only
		exchange.setRequestHeader("Host", "127.0.0.1:" + port);
		// exchange.setRequestHeader("Referer",
		// "http://www.hs-augsburg.de/");

		if (baseRequest.getContentLength() > 0)
		{
			try
			{
				exchange.setRequestContentType(baseRequest.getContentType());
				exchange.setRequestContentSource(request.getInputStream());
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		// send request
		try
		{
			httpClient.send(exchange);
			exchange.waitForDone();

			switch (exchange.getStatus())
			{
			case HttpExchange.STATUS_EXPIRED:
			case HttpExchange.STATUS_EXCEPTED:
				appServerGoneStale(appInfo);
				break;
			}

		} catch (InterruptedException e1)
		{
			e1.printStackTrace();
			appServerGoneStale(appInfo);
		} catch (IOException e)
		{
			e.printStackTrace();
			appServerGoneStale(appInfo);
		}

		try
		{
			// fill response
			HttpFields fields = exchange.getResponseFields();
			for (int i = 0; i < fields.size(); i++)
			{
				Field field = fields.getField(i);

//				System.out.println(field.getName() + ":" + field.getValue());
				response.addHeader(field.getName(), field.getValue());
			}

			response.setStatus(exchange.getResponseStatus());

			if (exchange.getResponseContentBytes() != null)
				response.getOutputStream().write(exchange.getResponseContentBytes());

			response.getOutputStream().close();

		} catch (NullPointerException e)
		{
			e.printStackTrace();
		} catch (EofException e)
		{
			// client closed
		} catch (IOException e)
		{
			// client closed
		}

	}

	private AppInfo startApp(String appId) throws DuplicatedAppException
	{
		if (this.appManager.existsApp(appId))
		{
			AppInfo info = this.appManager.getAppInfo(appId);
			if (info.isStale() == false)
				return info;
		}

		AppInfo info = this.appManager.startApp(appId);
		System.out.println("App started or restarted: " + info.getPort());
		
		try
		{
			// TODO: Wait until ProcessManager gives a signal!
			Thread.sleep(3000);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		} 
		
		return info;
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{

		// communication with existing AppServers
		System.out.println("Port: " + baseRequest.getServerPort());
		if (baseRequest.getServerPort() == 8079)
		{
			response.getOutputStream().close();
			return;
		}

		// extract appId
		String serverName = baseRequest.getServerName();
		int index = serverName.indexOf(".");
		if (index < 0)
		{
			response.getOutputStream().print("Error: Missing AppId");
			response.getOutputStream().close();
			return;
		}

		String appId = serverName.substring(0, index);
		try
		{

			AppInfo info = startApp(appId);

			// three tries until stale appservers cause an error
			for (int i = 0; i < 3; i++)
			{
				try
				{
					forwardRequest(info, baseRequest, request, response);
					break;

				} catch (StaleAppServerException e)
				{
					// e.printStackTrace();

					// restart the app
					startApp(appId);
				}
			}

		} catch (DuplicatedAppException e)
		{
			e.printStackTrace();
		}

	}
}
