package net.skds.lib.network;

import net.skds.lib.utils.SKDSByteBuf;

public interface OutPacket {
	void writePacket(SKDSByteBuf buffer);

	int getPacketId();

	default void send(AbstractConnection<?> connection) {
		connection.sendPacket(this);
	}
}
