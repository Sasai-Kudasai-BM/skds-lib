package net.skds.lib.network;

public interface OutPacket<T extends AbstractConnection<?>, B> {
	void writePacket(T connection, B buffer);

	int getPacketId();
}
