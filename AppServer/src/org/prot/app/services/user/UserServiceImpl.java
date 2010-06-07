package org.prot.app.services.user;

import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.servlet.http.Cookie;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.HttpConnection;
import org.prot.app.services.ChannelRegistry;
import org.prot.appserver.config.Configuration;
import org.prot.controller.services.gen.Services.RegisterUser;
import org.prot.controller.services.gen.Services.UrlRequest;
import org.prot.controller.services.gen.Services.User;
import org.prot.util.Cookies;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcChannel;
import com.google.protobuf.RpcController;
import com.googlecode.protobuf.socketrpc.SocketRpcChannel;
import com.googlecode.protobuf.socketrpc.SocketRpcController;

public final class UserServiceImpl implements UserService {
	private static final Logger logger = Logger.getLogger(UserServiceImpl.class);

	private String searchUID() {
		HttpConnection httpConnection = HttpConnection.getCurrentConnection();
		Cookie[] cookies = httpConnection.getRequest().getCookies();

		// If there is no cookie there is no active session
		if (cookies == null)
			return null;

		// Search the cookie named USER_ID
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(Cookies.USER_ID)) {
				String uid = cookie.getValue();
				return uid;
			}
		}

		return null;
	}

	public String getCurrentUser() {
		final String uid = searchUID();
		if (uid == null) {
			logger.debug("UID is null");
			return null;
		}

		String user = AccessController.doPrivileged(new PrivilegedAction<String>() {

			private String returnValue;

			@Override
			public String run() {
				ChannelRegistry registry = ChannelRegistry.getInstance();
				RpcChannel channel = registry.getChannel();
				RpcController controller = registry.getController(channel);

				org.prot.controller.services.gen.Services.UserService stub = org.prot.controller.services.gen.Services.UserService
						.newStub(channel);

				User.Builder builder = User.newBuilder();
				builder.setUid(uid);
				stub.getCurrentUser(controller, builder.build(), new RpcCallback<User>() {
					@Override
					public void run(User ret) {
						returnValue = ret.getUsername();
					}
				});

				return returnValue;
			}
		});

		return user;
	}

	public String getLoginUrl(final String redirectUrl, final String cancelUrl) {

		String retUrl = AccessController.doPrivileged(new PrivilegedAction<String>() {
			private String retUrl;

			@Override
			public String run() {
				ChannelRegistry registry = ChannelRegistry.getInstance();
				RpcChannel channel = registry.getChannel();
				RpcController controller = registry.getController(channel);

				org.prot.controller.services.gen.Services.UserService stub = org.prot.controller.services.gen.Services.UserService
						.newStub(channel);

				UrlRequest.Builder builder = UrlRequest.newBuilder();
				builder.setOkUrl(redirectUrl);
				builder.setFailedUrl(cancelUrl);
				stub.getLoginUrl(controller, builder.build(), new RpcCallback<UrlRequest>() {
					@Override
					public void run(UrlRequest ret) {
						retUrl = ret.getRedirectUrl();
					}
				});

				return retUrl;
			}
		});

		return retUrl;
	}

	public void registerUser(final String uid, final String username) {
		final String token = Configuration.getInstance().getAuthenticationToken();
		assert (token != null);

		AccessController.doPrivileged(new PrivilegedAction<String>() {
			@Override
			public String run() {
				ChannelRegistry registry = ChannelRegistry.getInstance();
				RpcChannel channel = registry.getChannel();
				RpcController controller = registry.getController(channel);

				org.prot.controller.services.gen.Services.UserService stub = org.prot.controller.services.gen.Services.UserService
						.newStub(channel);

				RegisterUser.Builder builder = RegisterUser.newBuilder();
				builder.setToken(token);
				builder.setUid(uid);
				builder.setUsername(username);
				builder.setSession(uid);

				stub.registerUser(controller, builder.build(), null);

				return null;
			}
		});
	}

	public void unregisterUser() {
		final String uid = searchUID();
		if (uid == null)
			return;

		AccessController.doPrivileged(new PrivilegedAction<String>() {
			@Override
			public String run() {
				SocketRpcChannel socketRpcChannel = new SocketRpcChannel("localhost", 6548);
				SocketRpcController rpcController = socketRpcChannel.newRpcController();

				org.prot.controller.services.gen.Services.UserService stub = org.prot.controller.services.gen.Services.UserService
						.newStub(socketRpcChannel);

				User.Builder builder = User.newBuilder();
				builder.setUid(uid);
				stub.unregisterUser(rpcController, builder.build(), null);

				return null;
			}
		});

		// Delete the UID-Cookie
		HttpConnection httpConnection = HttpConnection.getCurrentConnection();
		Cookie[] cookies = httpConnection.getRequest().getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(Cookies.USER_ID)) {
				logger.debug("Removing cookie");

				cookie.setMaxAge(0);
				cookie.setValue(null);
				httpConnection.getResponse().addCookie(cookie);
				break;
			}
		}
	}
}
