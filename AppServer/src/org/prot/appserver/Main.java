package org.prot.appserver;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.xml.XmlConfiguration;
import org.xml.sax.SAXException;

public class Main {

	public Main() {
		try {

			InputStream configFile = Main.class.getResourceAsStream("/etc/jetty/configuration.xml");
			XmlConfiguration config = new XmlConfiguration(configFile);
			Server server = (Server) config.configure();
			server.start();

		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Main();
	}
}
