package org.prot.app.services.log;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.apache.log4j.Logger;
import org.prot.appserver.config.Configuration;

public final class LogService
{
	private static final Logger logger = Logger.getLogger(LogService.class);

	private final String appId;

	private JdoConnection connection;

	LogService()
	{
		this.appId = Configuration.getInstance().getAppId();
		this.connection = new JdoConnection();
		this.connection.init();
	}

	private void log(String message, int severity)
	{
		PersistenceManager pm = this.connection.getPersistenceManager();
		Transaction tx = pm.currentTransaction();

		LogMessage log = new LogMessage();
		log.setAppId(appId);
		log.setMessage(message);
		log.setSeverity(0);

		try
		{
			tx.begin();
			pm.makePersistent(log);
			tx.commit();
		} catch (Exception e)
		{
			tx.rollback();
			logger.error("Could not write log message" + e);
		}
	}

	public List<String> getMessages(int severity)
	{
		PersistenceManager pm = this.connection.getPersistenceManager();
		Transaction tx = pm.currentTransaction();

		Query query = pm.newQuery(LogMessage.class);
		if (severity != -1)
		{
			query.setFilter("severity == " + severity);
		}

		List<LogMessage> logs = (List<LogMessage>) query.execute();
		List<String> messages = new ArrayList<String>();
		for (LogMessage message : logs)
		{
			messages.add(message.getMessage());
		}

		return messages;
	}

	public void debug(String message)
	{
		log(message, 0);
	}

	public void info(String message)
	{
		log(message, 1);
	}

	public void warn(String message)
	{
		log(message, 2);
	}

	public void error(String message)
	{
		log(message, 3);
	}
}
