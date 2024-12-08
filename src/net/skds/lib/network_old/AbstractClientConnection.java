package net.skds.lib.network_old;

import java.nio.channels.SocketChannel;

public abstract class AbstractClientConnection<T extends AbstractClientConnection<T>> extends AbstractConnection<T> {

	public AbstractClientConnection(SocketChannel channel, int bufferSize) {
		super(channel, bufferSize);
	}

	@Override
	public int getTimeout() {
		return 5000;
	}
}
