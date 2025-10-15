package net.skds.lib2.network.intercom;

import lombok.CustomLog;
import lombok.Getter;
import net.skds.lib2.network.tcp.ClientsideTCPConnectionOptions;
import net.skds.lib2.network.tcp.TCPConnectionFactory;
import net.skds.lib2.utils.ThreadUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

@CustomLog
public class IntercomClient implements IntercomConnectionOwner<ClientIntercomConnection, ClientsideTCPConnectionOptions> {

	@Getter
	private final Configuration configuration;
	private final TCPConnectionFactory<ClientIntercomConnection, IntercomClient> connectionFactory;

	@Getter
	private ClientIntercomConnection connection;

	public IntercomClient(Configuration configuration, TCPConnectionFactory<ClientIntercomConnection, IntercomClient> connectionFactory) {
		this.connectionFactory = connectionFactory;
		this.configuration = configuration;
	}

	public boolean connected() {
		var c = connection;
		if (c != null) return c.isAlive();
		return false;
	}

	public boolean tryConnect(InetSocketAddress address, int attempts, int delay) {
		int i = 0;
		while (attempts - i != 0) {
			log.info("[" + configuration.name() + "] Connecting to " + address);
			if (connect(address) != null) {
				break;
			}
			log.warn("[" + configuration.name() + "] Unable to connect to " + address);
			i++;
			ThreadUtils.await(delay);
		}
		if (connected()) {
			log.info("[" + configuration.name() + "] Connected to " + address);
			return true;
		} else {
			log.error("[" + configuration.name() + "] Unable to connect to " + address + " in " + i + " attempts");
		}
		return false;
	}

	public ClientIntercomConnection connect(InetSocketAddress address) {
		if (this.connection != null) throw new IllegalStateException("Connection is already established");
		try {
			SocketChannel channel = SocketChannel.open(address);
			ClientIntercomConnection c = connectionFactory.createConnection(channel, this).join();
			c.validateClientside();
			onConnect(c);
			c.startThreads();
			return c;
		} catch (IOException e) {
			log.error(e);
		}
		return null;
	}

	@Override
	public void onConnect(ClientIntercomConnection connection) {
		this.connection = connection;
	}

	@Override
	public void onDisconnect(ClientIntercomConnection connection) {
		this.connection = null;
	}

	@Override
	public ClientsideTCPConnectionOptions getOptions() {
		return configuration.options();
	}

	@Override
	public SecurityOptions getSecurityOptions() {
		return configuration.securityOptions;
	}

	@Override
	public String getName() {
		return configuration.name;
	}

	public record Configuration(ClientsideTCPConnectionOptions options,
								SecurityOptions securityOptions,
								IntercomSettings intercomSettings,
								String name
	) {
	}
}
