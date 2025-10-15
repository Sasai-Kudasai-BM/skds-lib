package net.skds.lib2.network.intercom.packet;


public enum PacketType {
	SYSTEM(true),
	SYSTEM_CRITICAL(false),
	BUNDLE(false),
	APPLICATION(true);

	public final int maskedId;
	public final boolean allowBundle;
	public static final PacketType[] VALUES = values();

	PacketType(boolean allowBundle) {
		this.maskedId = (ordinal() << PacketHeader.ID_SHIFT) & PacketHeader.ID_MASK;
		this.allowBundle = allowBundle;
	}

	public boolean isValidForStatus(HandshakeStatus status) {
		return !(status != HandshakeStatus.DONE && ordinal() >= APPLICATION.ordinal());
	}
}
