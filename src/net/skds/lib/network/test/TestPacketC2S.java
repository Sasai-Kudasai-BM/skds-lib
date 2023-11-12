package net.skds.lib.network.test;

import lombok.AllArgsConstructor;
import net.skds.lib.network.ClientConnection;
import net.skds.lib.network.InPacket;
import net.skds.lib.network.OutPacket;

import java.nio.ByteBuffer;

@AllArgsConstructor
public class TestPacketC2S implements InPacket<ClientConnection>, OutPacket<ClientConnection, ByteBuffer> {

	int msg;

	public TestPacketC2S(ByteBuffer buffer) {
		msg = buffer.getInt();
		buffer.position(buffer.position() + msg);
	}

	@Override
	public boolean instantHandle(ClientConnection cc) {
		System.out.println("c2s " + msg);
		cc.sendPacket(this);
		return true;
	}

	@Override
	public void apply(ClientConnection cc) {
	}

	@Override
	public void write(ClientConnection connection, ByteBuffer buffer) {
		buffer.putInt(msg);
		buffer.position(buffer.position() + msg);
	}

	@Override
	public int getId() {
		return 666;
	}

}
