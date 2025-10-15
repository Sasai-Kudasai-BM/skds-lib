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
public class PublicInfoResponseS2CPacket implements IntercomPacket<ClientIntercomConnection> {

	private final String publicInfo;

	public PublicInfoResponseS2CPacket(ExtendedDataInput input) throws IOException {
		this.publicInfo = input.readSizedString();
	}

	@Override
	public void write(ExtendedDataOutput output) throws IOException {
		output.writeSizedString(publicInfo);
	}

	@Override
	public void handle(ClientIntercomConnection connection) {
		connection.receivePublicInfo(publicInfo);
	}

	@Override
	public NetworkDirection getDirection() {
		return NetworkDirection.TO_CLIENT;
	}

	@Override
	public boolean isInstantHandle() {
		return true;
	}

	@Override
	public int packetId() {
		return SystemPacketRegistry.PUBLIC_INFO_RESPONSE;
	}

	@Override
	public PacketType packetType() {
		return PacketType.SYSTEM;
	}
}
