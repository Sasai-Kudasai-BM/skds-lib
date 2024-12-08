package net.skds.lib.network.packets;

import lombok.AllArgsConstructor;
import net.skds.lib.network.AbstractServerConnection;
import net.skds.lib.network.InPacket;
import net.skds.lib.network.OutPacket;
import net.skds.lib.utils.SKDSByteBuf;

@AllArgsConstructor
public class EncryptionStartS2CPacket implements OutPacket, InPacket<AbstractServerConnection<?>> {

	private final byte[] publicKey;
	private final byte[] token;

	public EncryptionStartS2CPacket(SKDSByteBuf buf) {
		publicKey = buf.readByteArray();
		token = buf.readByteArray();
	}

	@Override
	public void handle(AbstractServerConnection<?> connection) {
	}

	@Override
	public boolean instantHandle(AbstractServerConnection<?> connection) {
		connection.processEncryption(publicKey, token);
		return true;
	}

	@Override
	public void writePacket(SKDSByteBuf buffer) {
		buffer.writeByteArray(publicKey);
		buffer.writeByteArray(token);
	}

	@Override
	public int getPacketId() {
		return BasicPackets.ENCRYPTION_START_S2C_PACKET;
	}
}
