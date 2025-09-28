package net.skds.lib2.network.tcp;

import lombok.CustomLog;
import net.skds.lib2.misc.timer.SimpleTimer;

import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.SocketChannel;

@CustomLog
public abstract class ClientsideTCPConnection extends TCPConnection<ClientTCPConnectionOptions> {


	public ClientsideTCPConnection(SocketChannel channel, ClientTCPConnectionOptions options) {
		super(channel, options);
		try {
			final Socket socket = channel.socket();
			socket.setTcpNoDelay(true);
			socket.setSoTimeout(5000);
			SimpleTimer.INSTANCE.scheduleRelative(this::checkTimeout, options.getSilenceTimeout());
		} catch (SocketException e) {
			throw new RuntimeException(e);
		}
	}

}
