package org.prot.controller.services.log;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.apache.log4j.Logger;
import org.prot.controller.manager.AppManager;

public class LogServiceImpl implements LogService
{
	private static final Logger logger = Logger.getLogger(LogServiceImpl.class);

	private AppManager appManager;

	private JdoConnection connection;

	LogServiceImpl()
	{
		this.connection = new JdoConnection();
		this.connection.init();
	}

	@Override
	public void log(String token, String appId, String message, int severity)
	{
		if (appManager.checkToken(token) == false)
			return;

		PersistenceManager pm = this.connection.getPersistenceManager();
		try
		{
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
				logger.error("Could not write log message", e);
			}
		} finally
		{
			this.connection.releasePersistenceManager(pm);
		}
	}

	@Override
	public List<String> getMessages(String token, String appId, int severity)
	{
		if (appManager.checkToken(token) == false)
			return null;

		PersistenceManager pm = this.connection.getPersistenceManager();
		try
		{
			Transaction tx = pm.currentTransaction();

			Query query = pm.newQuery(LogMessage.class);
			String filter = "appId == '" + appId + "'";
			if (severity != -1)
				filter += " && severity == " + severity;
			query.setFilter(filter);

			List<LogMessage> logs = (List<LogMessage>) query.execute();
			List<String> messages = new ArrayList<String>();
			for (LogMessage message : logs)
			{
				messages.add(message.getMessage());
			}

			return messages;

		} finally
		{
			this.connection.releasePersistenceManager(pm);
		}
	}

	public void setAppManager(AppManager appManager)
	{
		this.appManager = appManager;
	}

}
