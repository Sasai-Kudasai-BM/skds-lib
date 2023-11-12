package net.skds.lib.network;

import net.skds.lib.network.test.TestPacket;
import net.skds.lib.network.test.TestPacketC2S;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ClientConnection extends AbstractConnection<ClientConnection> {

	public ClientConnection(SocketChannel channel, int bufferSize) {
		super(channel, bufferSize);
	}

	@Override
	protected InPacket<ClientConnection> readPacket(int id, ByteBuffer payload) {
		return switch (id) {
			case 666 -> new TestPacketC2S(payload);

			default -> new TestPacket<>(payload);
		};
	}
}
