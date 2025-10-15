package net.skds.lib2.network.intercom.packet.system;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.skds.lib2.io.ExtendedDataInput;
import net.skds.lib2.io.ExtendedDataOutput;
import net.skds.lib2.network.NetworkDirection;
import net.skds.lib2.network.intercom.ClientIntercomConnection;
import net.skds.lib2.network.intercom.packet.IntercomPacket;
import net.skds.lib2.network.intercom.packet.PacketType;
import net.skds.lib2.network.intercom.packet.SystemPacketRegistry;

import java.io.IOException;

@AllArgsConstructor
@Getter
public class HandshakeSuccessS2CPacket implements IntercomPacket<ClientIntercomConnection> {


	public HandshakeSuccessS2CPacket(ExtendedDataInput input) {
	}

	@Override
	public void write(ExtendedDataOutput output) throws IOException {
	}

	@Override
	public void handle(ClientIntercomConnection connection) {
		connection.handshakeSuccess();
	}


	@Override
	public boolean isInstantHandle() {
		return true;
	}

	@Override
	public NetworkDirection getDirection() {
		return NetworkDirection.TO_CLIENT;
	}

	@Override
	public int packetId() {
		return SystemPacketRegistry.HANDSHAKE_SUCCESS;
	}

	@Override
	public PacketType packetType() {
		return PacketType.SYSTEM_CRITICAL;
	}
}
