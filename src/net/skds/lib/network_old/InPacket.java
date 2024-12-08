package net.skds.lib.network_old;

public interface InPacket<T extends AbstractConnection<?>> {
	void applyPacket(T connection);

	default boolean instantHandle(T connection) {
		return false;
	}
}
