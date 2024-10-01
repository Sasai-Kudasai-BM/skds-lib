package net.skds.lib.network;

import java.nio.channels.SocketChannel;

public abstract class AbstractServerConnection<T extends AbstractServerConnection<T>> extends AbstractConnection<T> {

	public AbstractServerConnection(SocketChannel channel, int bufferSize) {
		super(channel, bufferSize);
	}

	@Override
	public int getTimeout() {
		return 5000;
	}

}
