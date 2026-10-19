package net.skds.lib2.network.intercom;

import net.skds.lib2.network.tcp.ServersideTCPConnectionOptions;
import net.skds.lib2.network.tcp.TCPConnectionFactory;
import net.skds.lib2.network.tcp.TCPServer;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class IntercomServer extends TCPServer<ServerIntercomConnection> implements IntercomConnectionOwner<ServerIntercomConnection, ServersideTCPConnectionOptions> {
	private static final net.skds.lib2.utils.logger.SKDSLogger log = net.skds.lib2.utils.logger.SKDSLoggerFactory.getLogger(IntercomServer.class);
	private final Configuration configuration;
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


	public record Configuration(ServersideTCPConnectionOptions options, SecurityOptions securityOptions, IntercomSettings intercomSettings, String name) {
	}

	public Configuration getConfiguration() {
		return this.configuration;
	}

	public Set<ServerIntercomConnection> getConnections() {
		return this.connections;
	}
}
