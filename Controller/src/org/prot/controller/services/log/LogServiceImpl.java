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
package org.prot.controller.services.log;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.apache.log4j.Logger;
import org.prot.controller.app.TokenChecker;
import org.prot.controller.services.gen.Services.ListMessages;
import org.prot.controller.services.gen.Services.LogService;
import org.prot.controller.services.gen.Services.MessageList;
import org.prot.controller.services.gen.Services.Void;
import org.prot.util.jdo.JdoConnection;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;

public class LogServiceImpl extends LogService {
	private static final Logger logger = Logger.getLogger(LogServiceImpl.class);

	private TokenChecker tokenChecker;

	@Override
	public void listMessages(RpcController controller, ListMessages request, RpcCallback<MessageList> done) {
		// if (tokenChecker.checkToken(token) == false)
		// return null;

		PersistenceManager pm = JdoConnection.getPersistenceManager();
		try {
			StringBuilder queryBuilder = new StringBuilder();
			queryBuilder.append("appId == '");
			queryBuilder.append(request.getAppId());
			queryBuilder.append("'");

			if (request.getSeverity() != -1) {
				// Don't support this until there are custom indices
				// queryBuilder.append(" && severity == ");
				// queryBuilder.append(severity);
			}

			String squery = queryBuilder.toString();

			Query query = pm.newQuery(LogMessage.class);
			query.setFilter(squery);

			Object result = query.execute();
			if (result == null) {
				return;
			}

			MessageList.Builder builder = MessageList.newBuilder();
			for (LogMessage msg : (List<LogMessage>) result) {
				org.prot.controller.services.gen.Services.LogMessage.Builder msgBuild = org.prot.controller.services.gen.Services.LogMessage
						.newBuilder();
				msgBuild.setAppid(request.getAppId());
				msgBuild.setMessage(msg.getMessage());
				msgBuild.setSeverity(msg.getSeverity());
				msgBuild.setToken("null");
				builder.addMessages(msgBuild.build());
			}

		} finally {
			pm.close();
		}
	}

	@Override
	public void log(RpcController controller, org.prot.controller.services.gen.Services.LogMessage request,
			RpcCallback<Void> done) {

		PersistenceManager pm = JdoConnection.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
			LogMessage log = new LogMessage();
			log.setAppId(request.getAppid());
			log.setMessage(request.getMessage());
			log.setSeverity(request.getSeverity());

			try {
				// tx.begin();
				pm.makePersistent(log);
				// tx.commit();
			} catch (Exception e) {
				logger.error("Could not write log message", e);
			}
		} finally {
			// if (tx.isActive())
			// tx.rollback();

			pm.close();
		}
	}

	public void setTokenChecker(TokenChecker tokenChecker) {
		this.tokenChecker = tokenChecker;
	}
}
