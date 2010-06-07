package org.prot.app.services;

import com.google.protobuf.RpcChannel;
import com.google.protobuf.RpcController;
import com.googlecode.protobuf.socketrpc.SocketRpcChannel;

public class ChannelRegistry {

	private ChannelFactory channelFactory = new ChannelFactory();

	private static ChannelRegistry registry;

	public static ChannelRegistry getInstance() {
		if (registry == null)
			registry = new ChannelRegistry();

		return registry;
	}

	public RpcChannel getChannel() {
		return channelFactory.buildChannel();
	}
	
	public RpcController getController(RpcChannel channel)
	{
		return ((SocketRpcChannel)channel).newRpcController();
	}
}
