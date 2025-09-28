package net.skds.lib2.network.tcp;

import lombok.CustomLog;
import lombok.Getter;

import java.nio.channels.SocketChannel;

@CustomLog
public abstract class ServersideTCPConnection extends TCPConnection<ServersideTCPConnectionOptions> {

	@Getter
	private final TCPServer server;

	public ServersideTCPConnection(SocketChannel channel, TCPServer server) {
		super(channel, server.options);
		this.server = server;
	}
}
