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
import net.skds.lib2.security.AuthorizationData;

import java.io.IOException;

@AllArgsConstructor
@Getter
public class AuthorizationStartC2SPacket implements IntercomPacket<ServerIntercomConnection> {

	private final AuthorizationData authorizationData;

	public AuthorizationStartC2SPacket(ExtendedDataInput input) throws IOException {
		this.authorizationData = new AuthorizationData(
				input.readSizedString(),
				input.readByteArray()
		);
	}

	@Override
	public void write(ExtendedDataOutput output) throws IOException {
		output.writeSizedString(this.authorizationData.type());
		output.writeByteArray(this.authorizationData.data());
	}

	@Override
	public void handle(ServerIntercomConnection connection) {
		connection.authorizationStart(authorizationData);
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
		return SystemPacketRegistry.AUTHORIZATION_START;
	}

	@Override
	public PacketType packetType() {
		return PacketType.SYSTEM_CRITICAL;
	}
}
