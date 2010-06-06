package org.prot.app.services.db;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.prot.app.services.user.UserService;
import org.prot.appserver.config.Configuration;
import org.prot.controller.services.db.DbService;
import org.prot.jdo.storage.messages.EntityMessage;

import com.google.protobuf.InvalidProtocolBufferException;

public final class DbBrowserService {
	private static final Logger logger = Logger.getLogger(DbBrowserService.class);

	private final DbService dbService;

	private UserService userService;

	DbBrowserService(DbService dbService) {
		this.dbService = dbService;
	}

	private UserService getUserService() {
		// if (this.userService == null)
		// this.userService = UserServiceFactory.getUserService();
		//
		// return this.userService;
		return null;
	}

	public List<String> getTables(String appId) {
		String user = getUserService().getCurrentUser();
		if (user == null) {
			logger.debug("User must be logged in to browse tables");
			return null;
		}

		return dbService.getTables(Configuration.getInstance().getAuthenticationToken(), appId);
	}

	public List<EntityMessage> getTableData(String appId, String kind) {
		String user = getUserService().getCurrentUser();
		if (user == null) {
			logger.debug("User msut be logged in to browse tables");
			return null;
		}

		List<byte[]> result = dbService.getTableData(Configuration.getInstance().getAuthenticationToken(),
				appId, kind);

		List<EntityMessage> entityMessages = new ArrayList<EntityMessage>();

		for (byte[] entity : result) {
			EntityMessage.Builder builder = EntityMessage.newBuilder();
			try {
				builder.mergeFrom(entity);
				EntityMessage entityMsg = builder.build();
				entityMessages.add(entityMsg);

			} catch (InvalidProtocolBufferException e) {
				e.printStackTrace();
			}
		}

		logger.debug("Builded: " + entityMessages.size());

		return entityMessages;
	}
}
