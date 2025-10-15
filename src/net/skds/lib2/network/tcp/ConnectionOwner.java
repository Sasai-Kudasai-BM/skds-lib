package net.skds.lib2.network.tcp;

public interface ConnectionOwner<T extends TCPConnection<?>, O extends TCPConnectionOptions> {

	void onConnect(T connection);

	void onDisconnect(T connection);

	O getOptions();
}
