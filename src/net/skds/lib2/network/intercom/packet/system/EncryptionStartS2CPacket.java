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
public class EncryptionStartS2CPacket implements IntercomPacket<ClientIntercomConnection> {

	private final byte[] publicKey;

	public EncryptionStartS2CPacket(ExtendedDataInput input) throws IOException {
		this.publicKey = input.readByteArray();
	}

	@Override
	public void write(ExtendedDataOutput output) throws IOException {
		output.writeByteArray(publicKey);
	}

	@Override
	public void handle(ClientIntercomConnection connection) {
		connection.encryptionStart(this);
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
		return SystemPacketRegistry.ENCRYPTION_START;
	}

	@Override
	public PacketType packetType() {
		return PacketType.SYSTEM_CRITICAL;
	}
}
