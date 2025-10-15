package net.skds.lib2.network.intercom.packet.system;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.skds.lib2.io.ExtendedDataInput;
import net.skds.lib2.io.ExtendedDataOutput;
import net.skds.lib2.network.intercom.IntercomConnection;
import net.skds.lib2.network.intercom.packet.IntercomPacket;
import net.skds.lib2.network.intercom.packet.PacketType;
import net.skds.lib2.network.intercom.packet.SystemPacketRegistry;

import java.io.IOException;

@Getter
@AllArgsConstructor
public class DisconnectPacket implements IntercomPacket<IntercomConnection<?>> {

	private final long time;
	private final String reason;

	public DisconnectPacket(ExtendedDataInput input) throws IOException {
		this.time = input.readLong();
		this.reason = input.readSizedString();
	}

	@Override
	public void write(ExtendedDataOutput output) throws IOException {
		output.writeLong(this.time);
		output.writeSizedString(this.reason);
	}

	@Override
	public void handle(IntercomConnection<?> connection) {
		connection.handleDisconnect(this);
	}


	@Override
	public boolean isInstantHandle() {
		return true;
	}

	@Override
	public int packetId() {
		return SystemPacketRegistry.DISCONNECT;
	}

	@Override
	public PacketType packetType() {
		return PacketType.SYSTEM_CRITICAL;
	}
}
