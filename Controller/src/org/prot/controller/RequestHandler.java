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

					// System.out.println(field.getName() + ":" +
					// field.getValue());
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

		// AppServerDesc desc = servers.get(app);
		// if (desc == null) {
		// desc = startAppServer(app);
		// }
		//
		// int port = desc.getPort();
		//
		// try {
		// ContentExchange exchange = new ContentExchange();
		//
		// exchange.setURL("http://localhost:" + port);
		// exchange.setURI(request.getRequestURI());
		// exchange.setMethod(baseRequest.getMethod());
		//
		// httpClient.send(exchange);
		// exchange.waitForDone();
		//
		// response.getOutputStream().write(exchange.getResponseContentBytes());
		// response.getOutputStream().close();
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	// private AppServerDesc startAppServer(String app) {
	// AppServerDesc desc = new AppServerDesc();
	// desc.setAppName(app);
	//
	// try {
	// ProcessBuilder builder = new ProcessBuilder();
	// builder.redirectErrorStream(true);
	// List<String> command = new LinkedList<String>();
	//
	// builder.directory(new File("../AppServer/"));
	//
	// command.add("javaw");
	// command.add("-classpath");
	//
	// String classpath = "../AppServer/bin/";
	// File file = new File("../Libs/lib/jetty-7.0.0/");
	// for (File f : file.listFiles()) {
	// classpath += (";" + f.getAbsolutePath());
	// }
	//
	// System.out.println("classpath: " + classpath);
	// command.add(classpath);
	//
	// command.add("org.prot.appserver.Main");
	// builder.command(command);
	// Process proc = builder.start();
	// desc.setProcess(proc);
	//
	// System.out.println("proc started");
	//
	// BufferedReader reader = new BufferedReader(new
	// InputStreamReader(proc.getInputStream()));
	//
	// class ReadThr extends Thread {
	//
	// private BufferedReader reader;
	//
	// public ReadThr(BufferedReader reader) {
	// this.reader = reader;
	// }
	//
	// public void run() {
	// String line;
	// try {
	// while ((line = reader.readLine()) != null) {
	// System.out.println(line);
	//
	// Thread.sleep(100);
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// }
	//
	// ReadThr thr = new ReadThr(reader);
	// thr.start();
	//
	// Runtime.getRuntime().addShutdownHook(new Thread() {
	// public void run() {
	// // shutdown all local servers
	// for (AppServerDesc desc : servers.values()) {
	// System.out.println("KILLING SUBPROCESS");
	// desc.getProcess().destroy();
	// }
	// }
	// });
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// servers.put(app, desc);
	//
	// return desc;
	// }

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
		//
		// System.out.println("Target: " + target);
		// System.out.println("Request infos: ");
		// System.out.println("Auth type: " + baseRequest.getAuthType());
		// System.out.println("ContextPath: " + baseRequest.getContextPath());
		// System.out.println("Method: " + baseRequest.getMethod());
		// System.out.println("Path info: " + baseRequest.getPathInfo());
		// System.out.println("Query string: " + baseRequest.getQueryString());
		// System.out.println("Request uri: " + baseRequest.getRequestURI());
		// System.out.println("Host url: " + baseRequest.getRemoteHost());
		// System.out.println("Host address: " + baseRequest.getRemoteAddr());
		// System.out.println("Server name: " + request.getServerName());

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
