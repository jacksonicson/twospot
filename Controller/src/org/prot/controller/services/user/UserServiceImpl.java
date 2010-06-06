package org.prot.controller.services.user;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.apache.log4j.Logger;
import org.prot.controller.app.TokenChecker;
import org.prot.controller.config.Configuration;
import org.prot.controller.services.gen.Services.RegisterUser;
import org.prot.controller.services.gen.Services.UrlRequest;
import org.prot.controller.services.gen.Services.User;
import org.prot.controller.services.gen.Services.UserService;
import org.prot.controller.services.gen.Services.Void;
import org.prot.util.ReservedAppIds;
import org.prot.util.jdo.JdoConnection;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;

public class UserServiceImpl extends UserService {
	private static final Logger logger = Logger.getLogger(UserServiceImpl.class);

	private TokenChecker tokenChecker;

	@Override
	public void getCurrentUser(RpcController controller, User request, RpcCallback<User> done) {
		if (request.getUid() == null)
			return;

		logger.info("Getting current user");

		// Query database
		PersistenceManager persistenceManager = JdoConnection.getPersistenceManager();

		try {
			Query query = persistenceManager.newQuery(UserSession.class);
			query.setFilter("sessionId == '" + request.getUid().trim() + "'");
			query.setUnique(true);

			Object object = query.execute();

			if (object != null) {
				UserSession session = (UserSession) object;
				String user = session.getUsername();

				User.Builder builder = User.newBuilder(request);
				builder.setUsername(user);
				done.run(builder.build());

				return;
			} else {
				logger.debug("Could not find user: " + request.getUid());
				return;
			}
		} catch (Exception e) {
			logger.error("Error while fetching user", e);
			return;
		} finally {
			persistenceManager.close();
		}
	}

	@Override
	public void getLoginUrl(RpcController controller, UrlRequest request, RpcCallback<UrlRequest> done) {

		logger.info("Getting login url");

		StringBuilder url = new StringBuilder();
		url.append("http://");
		url.append(ReservedAppIds.APP_PORTAL);
		url.append(".");
		url.append(Configuration.getConfiguration().getPlatformDomain());
		try {
			url.append("/login.htm?url=" + URLEncoder.encode(request.getRedirectUrl(), "UTF-8"));
			url.append("&cancel=" + URLEncoder.encode(request.getFailedUrl(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// Return null - we don't want to direct the user to invalid
			// redirect or cancel urls
			return;
		}

		UrlRequest.Builder builder = UrlRequest.newBuilder(request);
		builder.setRedirectUrl(url.toString());
		done.run(builder.build());
	}

	@Override
	public void registerUser(RpcController controller, RegisterUser request, RpcCallback<Void> done) {
//		if (tokenChecker.checkToken(request.getToken()) == false) {
//			logger.warn("Invalid AppServer token");
//			return;
//		}

		logger.info("Registering user...");

		// Create a new UserSession object
		UserSession userSession = new UserSession();
		userSession.setSessionId(request.getSession());
		userSession.setUsername(request.getUsername());
		userSession.setTimestamp(System.currentTimeMillis());

		PersistenceManager persistenceManager = JdoConnection.getPersistenceManager();
		try {
			try {
				// Delete everything from previous sessions
				Query query = persistenceManager.newQuery(UserSession.class);
				query.setFilter("username == '" + request.getUsername() + "'");
				ArrayList<UserSession> oldSessions = (ArrayList<UserSession>) query.execute();
				for (UserSession oldSession : oldSessions) {
					long timestamp = oldSession.getTimestamp();
					timestamp = System.currentTimeMillis() - timestamp;
					if (timestamp < 0 || timestamp > 24 * 60 * 60 * 1000) {
						logger.debug("Removing old session");
						persistenceManager.deletePersistent(oldSession);
					}
				}

				// Create a new entry for this session
				persistenceManager.makePersistent(userSession);
				logger.debug("User session is persistent");

			} catch (Exception e) {
				logger.error("Could not persistate user session", e);
			}

		} finally {
			persistenceManager.close();
		}
	}

	@Override
	public void unregisterUser(RpcController controller, User request, RpcCallback<Void> done) {
		if (request.getUid() == null) {
			logger.warn("Invalid user identifier token");
			return;
		}

		logger.info("Unregistering user...");

		PersistenceManager persistenceManager = JdoConnection.getPersistenceManager();
		try {
			// Query database
			Query query = persistenceManager.newQuery(UserSession.class);
			query.setFilter("sessionId == '" + request.getUid() + "'");
			query.setUnique(true);

			UserSession session = (UserSession) query.execute();
			if (session != null) {
				persistenceManager.deletePersistent(session);
			} else {
				logger.warn("Could not find and delete user session: " + request.getUid());
			}

		} catch (Exception e) {
			logger.error("Could not unregister user", e);
		} finally {
			persistenceManager.close();
		}
	}

	public void setTokenChecker(TokenChecker tokenChecker) {
		this.tokenChecker = tokenChecker;
	}
}