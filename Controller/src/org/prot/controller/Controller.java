package org.prot.controller;

import org.eclipse.jetty.server.Server;

public class Controller {

	private Server server; 
	
	public void setServer(Server server) {
		this.server = server;
	}
	
	public void start() {

		try {
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		
		/* try {

			InputStream configFile = Main.class.getResourceAsStream("/etc/jetty/configuration.xml");
			XmlConfiguration config = new XmlConfiguration(configFile);
			Server server = (Server) config.configure();
			server.start();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} */
	}

}
