package net.skds.lib.network;

public interface InPacket<T extends AbstractConnection<T>> {
	public void apply(T connection);
	public default boolean instantHandle(T connection) {
		return false;
	}
}
