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
package org.prot.app.services;

import com.google.protobuf.RpcChannel;
import com.googlecode.protobuf.socketrpc.SocketRpcChannel;

public class ChannelFactory {

	public RpcChannel buildChannel() {
		SocketRpcChannel socketRpcChannel = new SocketRpcChannel("localhost", 6548);
		return socketRpcChannel;
	}
}
