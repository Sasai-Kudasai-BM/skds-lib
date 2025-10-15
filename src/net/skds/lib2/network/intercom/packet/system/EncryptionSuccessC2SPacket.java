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
public class EncryptionSuccessC2SPacket implements IntercomPacket<ServerIntercomConnection> {

	private final byte[] key;

	public EncryptionSuccessC2SPacket(ExtendedDataInput input) throws IOException {
		this.key = input.readByteArray();
	}

	@Override
	public void write(ExtendedDataOutput output) throws IOException {
		output.writeByteArray(key);
	}

	@Override
	public void handle(ServerIntercomConnection connection) {
		connection.onEncryptionSuccess(this);
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
		return SystemPacketRegistry.ENCRYPTION_SUCCESS;
	}

	@Override
	public PacketType packetType() {
		return PacketType.SYSTEM_CRITICAL;
	}
}
