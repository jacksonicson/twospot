package org.prot.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.xml.XmlConfiguration;
import org.xml.sax.SAXException;

public class Controller {

	private static final Logger logger = Logger.getLogger(Controller.class.getName());

	public Controller() {
		logger.info("starting controller");

		try {

			InputStream configFile = Controller.class.getResourceAsStream("/etc/jetty/configuration.xml");
			XmlConfiguration config = new XmlConfiguration(configFile);
			Server server = (Server) config.configure();
			server.start();

		} catch (IOException e) {
			e.printStackTrace();
			logger.throwing(Controller.class.getName(), "Controller()", e);
		} catch (SAXException e) {
			e.printStackTrace();
			logger.throwing(Controller.class.getName(), "Controller()", e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.throwing(Controller.class.getName(), "Controller()", e);
		}
	}

	public static void main(String arg[]) {
		new Controller();
	}
}
