package net.skds.lib2.network.bad;

public interface InPacket<T extends AbstractConnection<?>> {
	void handle(T connection);

	default boolean instantHandle(T connection) {
		return false;
	}
}
