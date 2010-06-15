/*******************************************************************************
 * Copyright (c) 2010 Andreas Wolke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Wolke - initial API and implementation and initial documentation
 *******************************************************************************/
package org.prot.portal.login.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.apache.log4j.Logger;
import org.prot.portal.app.data.Application;

public class JdoAppDao implements AppDao
{
	private static final Logger logger = Logger.getLogger(JdoAppDao.class);

	private JdoConnection jdoConnection;

	@Override
	public Set<String> getApps(String owner)
	{
		PersistenceManager pm = jdoConnection.getPersistenceManager();

		Query query = pm.newQuery();
		query.setClass(Application.class);
		query.setFilter("owner== '" + owner + "'");

		try
		{
			Collection<Application> applications = (Collection<Application>) query.execute();

			Set<String> appIds = new HashSet<String>();
			for (Application app : applications)
			{
				appIds.add(app.getAppId());
			}

			return appIds;

		} catch (Exception e)
		{
			logger.error("Unable to fetch appIds", e);
		}

		return null;
	}

	@Override
	public Application loadApp(String appId)
	{
		PersistenceManager pm = jdoConnection.getPersistenceManager();

		Query query = pm.newQuery();
		query.setClass(Application.class);
		query.setFilter("appId == '" + appId + "'");
		query.setUnique(true);

		try
		{
			Application app = (Application) query.execute();
			return app;

		} catch (Exception e)
		{
			logger.error("Unable to fetch application", e);
		}

		return null;
	}

	@Override
	public void saveApp(String appId, String owner)
	{
		Application application = new Application();
		application.setAppId(appId);
		application.setOwner(owner);

		// TODO: Throw an exception
		if (owner == null)
		{
			logger.error("Making persistent error - owner is null");
			return;
		}

		PersistenceManager pm = jdoConnection.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		
		try
		{
			tx.begin();
			pm.makePersistent(application);
			tx.commit();
		} catch (Exception e)
		{
			logger.info("Making persistent error", e);
			tx.rollback();
		} finally {
			if(tx.isActive())
				tx.rollback();
		}
	}

	public void setJdoConnection(JdoConnection jdoConnection)
	{
		this.jdoConnection = jdoConnection;
	}

}
