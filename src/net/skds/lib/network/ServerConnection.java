package net.skds.lib.network;

import net.skds.lib.network.test.TestPacket;
import net.skds.lib.network.test.TestPacketS2C;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ServerConnection extends AbstractConnection<ServerConnection> {

	public ServerConnection(SocketChannel channel, int bufferSize) {
		super(channel, bufferSize);
	}


	public void loop() {
		while (channel.isOpen()) {
			try {
				selector.select(this::onSelect, 5);
				flushPackets();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected InPacket<ServerConnection> readPacket(int id, ByteBuffer payload) {
		return switch (id) {
			case 666 -> new TestPacketS2C(payload);

			default -> new TestPacket<>(payload);
		};
	}

}
