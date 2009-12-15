package org.prot.controller.services.log;

import java.util.ArrayList;
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

	@Override
	public void log(String token, String appId, String message, int severity)
	{
		PersistenceManager pm = JdoConnection.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try
		{
			LogMessage log = new LogMessage();
			log.setKey(LogMessage.buildKey(appId, ""));
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
				logger.error("Could not write log message", e);
			}
		} finally
		{
			if (tx.isActive())
				tx.rollback();

			pm.close();
		}
	}

	@Override
	public List<LogMessage> getMessages(String token, String appId, int severity)
	{
		if (tokenChecker.checkToken(token) == false)
			return null;

		PersistenceManager pm = JdoConnection.getPersistenceManager();
		try
		{
			StringBuilder queryBuilder = new StringBuilder();
			queryBuilder.append("appId == '");
			queryBuilder.append(appId);
			queryBuilder.append("'");

			if (severity != -1)
			{
				queryBuilder.append(" && severity == ");
				queryBuilder.append(severity);
			}

			Query query = pm.newQuery(LogMessage.class);
			query.setFilter(queryBuilder.toString());
			// WARN: Use a range here - but hte HbasePlugin doesn't support
			// this!

			Object result = query.execute();
			if (result == null)
				return new ArrayList<LogMessage>();

			return (List<LogMessage>) result;

		} finally
		{
			pm.close();
		}
	}

	public void setTokenChecker(TokenChecker tokenChecker)
	{
		this.tokenChecker = tokenChecker;
	}
}
