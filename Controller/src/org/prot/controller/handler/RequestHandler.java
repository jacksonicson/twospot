package org.prot.controller.handler;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
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
import org.prot.controller.manager.exceptions.AppServerFailedException;

public class RequestHandler extends AbstractHandler
{
	private static final Logger logger = Logger.getLogger(RequestHandler.class);

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
			logger.error("Could not initialize the HTTPClient", e);
			System.exit(1);
		}
	}

	public void setAppManager(AppManager appManager)
	{
		this.appManager = appManager;
	}

	private boolean forwardRequest(AppInfo appInfo, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response)
	{
		// create request
		int port = appInfo.getPort();
		String uri = baseRequest.getUri().getCompletePath().substring(1);
		uri = uri.substring(uri.indexOf("/"));
		String url = "http://127.0.0.1:" + port + uri;

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
				exchange.addRequestHeader(headerName, headerValue);
			}
		}

		// use always localhost as request host
		exchange.setRequestHeader("Host", "127.0.0.1:" + port);

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
				return false;
			}

		} catch (InterruptedException e1)
		{
			logger.error("reporting a stale appserver");
			appServerGoneStale(appInfo);
			return false;

		} catch (IOException e)
		{
			logger.error("reporting a stale appserver");
			appServerGoneStale(appInfo);
			return false;
		}

		try
		{
			// fill response
			HttpFields fields = exchange.getResponseFields();
			for (int i = 0; i < fields.size(); i++)
			{
				Field field = fields.getField(i);

				// System.out.println(field.getName() + ":" + field.getValue());
				response.addHeader(field.getName(), field.getValue());
			}

			response.setStatus(exchange.getResponseStatus());

			if (exchange.getResponseContentBytes() != null)
				response.getOutputStream().write(exchange.getResponseContentBytes());

			response.getOutputStream().close();

		} catch (EofException e)
		{
			// Client closed the connection
		} catch (IOException e)
		{
			// Client closed the connection
		}

		return true;
	}

	private void appServerGoneStale(AppInfo appInfo)
	{
		this.appManager.reportStaleApp(appInfo.getAppId());
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{
		// extract appId
		String uri = baseRequest.getUri().getPath().substring(1);
		int index = uri.indexOf("/");
		if (index < 0)
		{
			response.getOutputStream().print("Error: Missing AppId");
			response.getOutputStream().close();
			return;
		}
		String appId = uri.substring(0, index);

		try
		{
			// retries if forward fails
			for (int i = 0; i < 3; i++)
			{
				// inform the AppManager
				AppInfo appInfo = this.appManager.requireApp(appId);

				// forward the request
				if (forwardRequest(appInfo, baseRequest, request, response))
					break;
			}

		} catch (AppServerFailedException e)
		{
			logger.error("AppServer failed", e);
		}
	}
}
