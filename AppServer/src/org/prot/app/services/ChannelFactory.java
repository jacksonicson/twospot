package org.prot.app.services;

import com.google.protobuf.RpcChannel;
import com.googlecode.protobuf.socketrpc.SocketRpcChannel;

public class ChannelFactory {

	public RpcChannel buildChannel() {
		SocketRpcChannel socketRpcChannel = new SocketRpcChannel("localhost", 6548);
		return socketRpcChannel;
	}
}
