package org.prot.controller;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpFields.Field;
import org.eclipse.jetty.io.EofException;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.prot.controller.manager.AppInfo;
import org.prot.controller.manager.AppManager;
import org.prot.controller.manager.AppRegistry;
import org.prot.controller.manager.AppStarter;
import org.prot.controller.manager.DuplicatedAppException;

public class RequestHandler extends AbstractHandler {

	private HttpClient httpClient;

	private AppManager appManager;

	public RequestHandler() {
		System.out.println(">>>>>>> creating a new request handler");

		try {
			httpClient = new HttpClient();
			httpClient.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
			httpClient.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

		appManager = new AppManager();
		appManager.setRegistry(new AppRegistry());
		appManager.setStarter(new AppStarter());

	}

	private void forwardRequest(AppInfo appInfo, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) {

		try {
			// create request
			int port = appInfo.getPort();
			String url = "http://127.0.0.1:" + port + baseRequest.getUri();
			System.out.println("url: " + url);

			ContentExchange exchange = new ContentExchange(true);
			exchange.setMethod(baseRequest.getMethod());
			exchange.setURL(url);
			exchange.setVersion(baseRequest.getProtocol());

			Enumeration<String> headerNames = baseRequest.getHeaderNames();
			while (headerNames.hasMoreElements()) {
				String headerName = headerNames.nextElement();

				Enumeration<String> headerValues = baseRequest.getHeaders(headerName);
				while (headerValues.hasMoreElements()) {
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

			if (baseRequest.getContentLength() > 0) {
				exchange.setRequestContentType(baseRequest.getContentType());
				exchange.setRequestContentSource(request.getInputStream());
			}

			// send request
			httpClient.send(exchange);
			exchange.waitForDone();

			try {
				// fill response
				HttpFields fields = exchange.getResponseFields();
				for (int i = 0; i < fields.size(); i++) {
					Field field = fields.getField(i);

					System.out.println(field.getName() + ":" + field.getValue());
					response.addHeader(field.getName(), field.getValue());
				}

				response.setStatus(exchange.getResponseStatus());

				if (exchange.getResponseContentBytes() != null)
					response.getOutputStream().write(exchange.getResponseContentBytes());

				response.getOutputStream().close();

			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (EofException e) {
				// client closed
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private AppInfo startApp(String appId) throws DuplicatedAppException {
		if (this.appManager.existsApp(appId)) {
			return this.appManager.getAppInfo(appId);
		}

		AppInfo info = this.appManager.startApp(appId);
		System.out.println("App started: " + info.getPort());
		return info;
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		
		// Heartbeat-Port
		System.out.println("Port: " + baseRequest.getServerPort());
		if(baseRequest.getServerPort() == 8079) {
			response.getOutputStream().close();
			return; 
		}
		
		String serverName = baseRequest.getServerName();
		int index = serverName.indexOf(".");
		if (index < 0) {
			response.getOutputStream().print("Error: Missing AppId");
			response.getOutputStream().close();
			return;
		}

		String appId = serverName.substring(0, index);
		try {
			
			AppInfo info = startApp(appId);
			forwardRequest(info, baseRequest, request, response);
			
		} catch (DuplicatedAppException e) {
			e.printStackTrace();
		}

	}
}
