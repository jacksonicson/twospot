package org.prot.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

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

public class RequestHandler extends AbstractHandler {

	private Hashtable<String, AppServerDesc> servers = new Hashtable<String, AppServerDesc>();

	HttpClient httpClient;

	public RequestHandler() {
		System.out.println(">>>>>>> creating a new request handler");

		try {
			httpClient = new HttpClient();
			httpClient.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
			httpClient.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void forwardRequest(String app, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) {

		try {
			// create request
			String url = "http://www.hs-augsburg.de" + baseRequest.getUri();
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
			exchange.setRequestHeader("Host", "www.hs-augsburg.de");
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

	private AppServerDesc startAppServer(String app) {
		AppServerDesc desc = new AppServerDesc();
		desc.setAppName(app);

		try {
			ProcessBuilder builder = new ProcessBuilder();
			builder.redirectErrorStream(true);
			List<String> command = new LinkedList<String>();

			builder.directory(new File("../AppServer/"));

			command.add("javaw");
			command.add("-classpath");

			String classpath = "../AppServer/bin/";
			File file = new File("../Libs/lib/jetty-7.0.0/");
			for (File f : file.listFiles()) {
				classpath += (";" + f.getAbsolutePath());
			}

			System.out.println("classpath: " + classpath);
			command.add(classpath);

			command.add("org.prot.appserver.Main");
			builder.command(command);
			Process proc = builder.start();
			desc.setProcess(proc);

			System.out.println("proc started");

			BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));

			class ReadThr extends Thread {

				private BufferedReader reader;

				public ReadThr(BufferedReader reader) {
					this.reader = reader;
				}

				public void run() {
					String line;
					try {
						while ((line = reader.readLine()) != null) {
							System.out.println(line);

							Thread.sleep(100);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			ReadThr thr = new ReadThr(reader);
			thr.start();

			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					// shutdown all local servers
					for (AppServerDesc desc : servers.values()) {
						System.out.println("KILLING SUBPROCESS");
						desc.getProcess().destroy();
					}
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

		servers.put(app, desc);

		return desc;
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

		StringBuffer url = baseRequest.getRequestURL();
		// System.out.println("URL: " + url);
		URL myUrl = new URL(url.toString());
		// System.out.println("Host: " + myUrl.getHost());
		String host = myUrl.getHost();
		if (host.indexOf(".") < 0) {
			throw new NullPointerException();
		}
		String app = host.substring(0, host.indexOf("."));
		// System.out.println("APP: " + app);
		forwardRequest(app, baseRequest, request, response);

		response.getOutputStream().write("Hello World".getBytes());
		response.getOutputStream().close();
	}
}
