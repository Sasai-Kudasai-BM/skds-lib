package net.skds.lib2.network.intercom.packet.system;

import net.skds.lib2.io.ExtendedDataInput;
import net.skds.lib2.io.ExtendedDataOutput;
import net.skds.lib2.network.NetworkDirection;
import net.skds.lib2.network.intercom.SecurityLevel;
import net.skds.lib2.network.intercom.ServerIntercomConnection;
import net.skds.lib2.network.intercom.packet.IntercomPacket;
import net.skds.lib2.network.intercom.packet.PacketType;
import net.skds.lib2.network.intercom.packet.SystemPacketRegistry;
import java.io.IOException;

public class HandshakeStartC2SPacket implements IntercomPacket<ServerIntercomConnection> {
	private final String name;
	private final String publicInfo;
	private final long timestamp;
	private final SecurityLevel securityLevel;

	public HandshakeStartC2SPacket(ExtendedDataInput input) throws IOException {
		this.name = input.readSizedString();
		this.publicInfo = input.readSizedString();
		this.timestamp = input.readLong();
		this.securityLevel = input.readByteEnum(SecurityLevel.class);
	}

	@Override
	public void write(ExtendedDataOutput output) throws IOException {
		output.writeSizedString(this.name);
		output.writeSizedString(this.publicInfo);
		output.writeLong(this.timestamp);
		output.writeByteEnum(this.securityLevel);
	}

	@Override
	public void handle(ServerIntercomConnection connection) {
		connection.handshakeStart(this);
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
		return SystemPacketRegistry.HANDSHAKE_START;
	}

	@Override
	public PacketType packetType() {
		return PacketType.SYSTEM_CRITICAL;
	}

	public HandshakeStartC2SPacket(final String name, final String publicInfo, final long timestamp, final SecurityLevel securityLevel) {
		this.name = name;
		this.publicInfo = publicInfo;
		this.timestamp = timestamp;
		this.securityLevel = securityLevel;
	}

	public String getName() {
		return this.name;
	}

	public String getPublicInfo() {
		return this.publicInfo;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public SecurityLevel getSecurityLevel() {
		return this.securityLevel;
	}
}
