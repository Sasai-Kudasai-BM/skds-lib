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
public class AuthorizationResultS2CPacket implements IntercomPacket<ClientIntercomConnection> {

	private final String error;

	public AuthorizationResultS2CPacket(ExtendedDataInput input) throws IOException {
		this.error = input.readOptional(ExtendedDataInput::readSizedString);
	}

	@Override
	public void write(ExtendedDataOutput output) throws IOException {
		output.writeOptional(error, ExtendedDataOutput::writeSizedString);
	}

	@Override
	public void handle(ClientIntercomConnection connection) {
		connection.onAuthorize(error);
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
		return SystemPacketRegistry.AUTHORIZATION_RESULT;
	}

	@Override
	public PacketType packetType() {
		return PacketType.SYSTEM_CRITICAL;
	}
}
