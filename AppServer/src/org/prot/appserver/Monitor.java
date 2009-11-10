package org.prot.appserver;

import java.io.IOException;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;

/**
 * TODO: Use RMI to communicate with the Controller
 * @author Andreas Wolke
 *
 */
public class Monitor extends Thread {

	private final int sleepTime = 10000;

	private HttpClient httpClient;

	public Monitor() {

		try {
			this.httpClient = new HttpClient();
			this.httpClient.start();

			this.start();

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void run() {
		while (true) {

			try {
				ContentExchange exchange = new ContentExchange();
				exchange.setRetryStatus(false);
				exchange.setURL("http://127.0.0.1:8080");

				this.httpClient.send(exchange);
				exchange.waitForDone();
				int status = exchange.getStatus();
				switch (status) {
				case ContentExchange.STATUS_EXCEPTED:
					System.exit(0);
					break;
				case ContentExchange.STATUS_EXPIRED:
					System.exit(0);
					break;
				}

			} catch (InterruptedException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
