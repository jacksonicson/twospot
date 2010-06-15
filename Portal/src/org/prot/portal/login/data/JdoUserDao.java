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

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.apache.log4j.Logger;

public class JdoUserDao implements UserDao
{
	private static final Logger logger = Logger.getLogger(JdoUserDao.class);

	private JdoConnection jdoConnection;

	@Override
	public PlatformUser getUser(String username)
	{
		PersistenceManager pm = jdoConnection.getPersistenceManager();

		Query query = pm.newQuery(PlatformUser.class);
		query.setFilter("username == '" + username + "'");
		query.setUnique(true);

		try
		{
			Object result = query.execute();
			if (result == null)
				return null;

			return (PlatformUser) result;

		} catch (Exception e)
		{
			logger.error("Unable to fetch user", e);
		}

		return null;
	}

	@Override
	public void saveUser(PlatformUser user)
	{
		PersistenceManager pm = jdoConnection.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		tx.begin();

		try
		{
			logger.info("Making persistent");
			pm.makePersistent(user);
			logger.info("done");
			tx.commit();
		} catch (Exception e)
		{
			logger.info("Making persistent error", e);
			tx.rollback();
		} finally
		{
			if (tx.isActive())
				tx.rollback();
		}
	}

	@Override
	public void updateUser(PlatformUser user)
	{
		saveUser(user);
	}

	public void setJdoConnection(JdoConnection jdoConnection)
	{
		this.jdoConnection = jdoConnection;
	}

}
