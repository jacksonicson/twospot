package org.prot.controller.services.log;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.apache.log4j.Logger;
import org.prot.controller.manager.TokenChecker;
import org.prot.util.jdo.JdoConnection;

public class LogServiceImpl implements LogService
{
	private static final Logger logger = Logger.getLogger(LogServiceImpl.class);

	private TokenChecker tokenChecker;

	private JdoConnection connection;

	LogServiceImpl()
	{
		this.connection = new JdoConnection();
		this.connection.init();
	}

	@Override
	public void log(String token, String appId, String message, int severity)
	{
		PersistenceManager pm = this.connection.getPersistenceManager();
		try
		{
			Transaction tx = pm.currentTransaction();

			logger.debug("Writing log for: " + appId + " message: " + message + " severity: " + severity);
			LogMessage log = new LogMessage();
			log.setAppId(appId);
			log.setMessage(message);
			log.setSeverity(severity);

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
	public List<LogMessage> getMessages(String token, String appId, int severity)
	{
		if (tokenChecker.checkToken(token) == false)
		{
			logger.warn("Invalid token");
			return null;
		}

		PersistenceManager pm = this.connection.getPersistenceManager();
		try
		{
			Query query = pm.newQuery(LogMessage.class);
			String filter = "appId == '" + appId + "'";
			if (severity != -1)
				filter += " && severity == " + severity;
			query.setFilter(filter);
			query.setRange(0, 100);

			List<LogMessage> logs = (List<LogMessage>) query.execute();
			logger.info("Found messages: " + logs.size());
			return logs;

		} finally
		{
			this.connection.releasePersistenceManager(pm);
		}
	}

	public void setTokenChecker(TokenChecker tokenChecker)
	{
		this.tokenChecker = tokenChecker;
	}
}
