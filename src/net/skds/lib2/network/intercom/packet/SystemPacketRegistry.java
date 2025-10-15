package net.skds.lib2.network.intercom.packet;

import net.skds.lib2.network.intercom.packet.system.*;

public class SystemPacketRegistry implements PacketRegistry {

	public static final SystemPacketRegistry INSTANCE = new SystemPacketRegistry();

	private static final PacketReader[] PACKETS = new PacketReader[16];

	public static final int DISCONNECT = 0;
	public static final int PING_ID = 1;
	public static final int PONG_ID = 2;
	public static final int PUBLIC_INFO_REQUEST = 3;
	public static final int PUBLIC_INFO_RESPONSE = 4;
	public static final int HANDSHAKE_START = 5;
	public static final int HANDSHAKE_SUCCESS = 6;
	public static final int ENCRYPTION_START = 7;
	public static final int ENCRYPTION_SUCCESS = 8;
	public static final int CERTIFICATE = 9;
	public static final int AUTHORIZATION_START = 10;
	public static final int AUTHORIZATION_RESULT = 11;

	static {
		// add packets
		PACKETS[PING_ID] = PingPacket::new;
		PACKETS[PONG_ID] = PongPacket::new;
		PACKETS[HANDSHAKE_START] = HandshakeStartC2SPacket::new;
		PACKETS[PUBLIC_INFO_REQUEST] = PublicInfoRequestC2SPacket::new;
		PACKETS[PUBLIC_INFO_RESPONSE] = PublicInfoResponseS2CPacket::new;
		PACKETS[DISCONNECT] = DisconnectPacket::new;
		PACKETS[HANDSHAKE_SUCCESS] = HandshakeSuccessS2CPacket::new;
		PACKETS[ENCRYPTION_START] = EncryptionStartS2CPacket::new;
		PACKETS[ENCRYPTION_SUCCESS] = EncryptionSuccessC2SPacket::new;
		PACKETS[CERTIFICATE] = CertificateS2CPacket::new;
		PACKETS[AUTHORIZATION_START] = AuthorizationStartC2SPacket::new;
		PACKETS[AUTHORIZATION_RESULT] = AuthorizationResultS2CPacket::new;
	}

	@Override
	public PacketReader getPacketReader(int id) {
		if (id < 0 || id >= PACKETS.length) return null;
		return PACKETS[id];
	}
}
