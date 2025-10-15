package net.skds.lib2.network.intercom.packet.system;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.skds.lib2.io.ExtendedDataInput;
import net.skds.lib2.io.ExtendedDataOutput;
import net.skds.lib2.network.NetworkDirection;
import net.skds.lib2.network.intercom.ServerIntercomConnection;
import net.skds.lib2.network.intercom.packet.IntercomPacket;
import net.skds.lib2.network.intercom.packet.PacketType;
import net.skds.lib2.network.intercom.packet.SystemPacketRegistry;

import java.io.IOException;

@AllArgsConstructor
@Getter
public class PublicInfoRequestC2SPacket implements IntercomPacket<ServerIntercomConnection> {


	public PublicInfoRequestC2SPacket(ExtendedDataInput input) {
	}

	@Override
	public void write(ExtendedDataOutput output) throws IOException {
	}

	@Override
	public void handle(ServerIntercomConnection connection) {
		connection.onPublicInfoRequest();
	}

	@Override
	public boolean isInstantHandle() {
		return true;
	}

	@Override
	public NetworkDirection getDirection() {
		return NetworkDirection.TO_SERVER;
	}

	@Override
	public int packetId() {
		return SystemPacketRegistry.PUBLIC_INFO_REQUEST;
	}

	@Override
	public PacketType packetType() {
		return PacketType.SYSTEM;
	}
}
