package net.skds.lib2.network.intercom;

import lombok.CustomLog;
import lombok.Getter;
import net.skds.lib2.network.tcp.ServersideTCPConnectionOptions;
import net.skds.lib2.network.tcp.TCPConnectionFactory;
import net.skds.lib2.network.tcp.TCPServer;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@CustomLog
public class IntercomServer extends TCPServer<ServerIntercomConnection> implements IntercomConnectionOwner<ServerIntercomConnection, ServersideTCPConnectionOptions> {

	@Getter
	private final Configuration configuration;

	@Getter
	private final Set<ServerIntercomConnection> connections = ConcurrentHashMap.newKeySet();

	public IntercomServer(Configuration configuration, TCPConnectionFactory<ServerIntercomConnection, IntercomServer> connectionFactory) {
		super(configuration.options, configuration.name, connectionFactory);
		this.configuration = configuration;
	}

	@Override
	public void onDisconnect(ServerIntercomConnection connection) {
		this.connections.remove(connection);
	}

	@Override
	public SecurityOptions getSecurityOptions() {
		return configuration.securityOptions;
	}

	@Override
	public String getName() {
		return configuration.name;
	}

	@Override
	public void onConnect(ServerIntercomConnection connection) {
		connections.add(connection);
	}

	public record Configuration(ServersideTCPConnectionOptions options,
								SecurityOptions securityOptions,
								IntercomSettings intercomSettings,
								String name
	) {
	}
}
