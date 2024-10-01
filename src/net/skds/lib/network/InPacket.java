package net.skds.lib.network;

public interface InPacket<T extends AbstractConnection<?>> {
	void applyPacket(T connection);

	default boolean instantHandle(T connection) {
		return false;
	}
}
