package net.skds.lib.network.test;

import lombok.AllArgsConstructor;
import net.skds.lib.network.InPacket;
import net.skds.lib.network.OutPacket;
import net.skds.lib.network.ServerConnection;

import java.nio.ByteBuffer;

@AllArgsConstructor
public class TestPacketS2C implements InPacket<ServerConnection>, OutPacket<ServerConnection, ByteBuffer> {

	int msg;

	public TestPacketS2C(ByteBuffer buffer) {
		msg = buffer.getInt();
		buffer.position(buffer.position() + msg);
	}

	@Override
	public boolean instantHandle(ServerConnection cc) {
		System.out.println("s2c " + msg);
		return true;
	}

	@Override
	public void apply(ServerConnection cc) {
	}

	@Override
	public void write(ServerConnection connection, ByteBuffer buffer) {
		buffer.putInt(msg);
		buffer.position(buffer.position() + msg);
	}

	@Override
	public int getId() {
		return 666;
	}

}
