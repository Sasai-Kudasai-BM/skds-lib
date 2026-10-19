package net.skds.lib2.network.intercom.packet.system;

import net.skds.lib2.io.ExtendedDataInput;
import net.skds.lib2.io.ExtendedDataOutput;
import net.skds.lib2.network.intercom.IntercomConnection;
import net.skds.lib2.network.intercom.packet.IntercomPacket;
import net.skds.lib2.network.intercom.packet.PacketType;
import net.skds.lib2.network.intercom.packet.SystemPacketRegistry;
import java.io.IOException;

public class PongPacket implements IntercomPacket<IntercomConnection<?>> {
	private final long time;

	public PongPacket(ExtendedDataInput input) throws IOException {
		this.time = input.readLong();
	}

	@Override
	public void write(ExtendedDataOutput output) throws IOException {
		output.writeLong(this.time);
	}

	@Override
	public void handle(IntercomConnection<?> connection) {
		connection.pong(time);
	}

	@Override
	public boolean isInstantHandle() {
		return true;
	}

	@Override
	public int packetId() {
		return SystemPacketRegistry.PONG_ID;
	}

	@Override
	public PacketType packetType() {
		return PacketType.SYSTEM;
	}

	public PongPacket(final long time) {
		this.time = time;
	}
}
