package net.skds.lib2.network.intercom.packet;

public enum HandshakeStatus {
	NONE,
	HANDSHAKE,
	ENCRYPTION,
	CERT_VALIDATION,
	AUTHORIZATION,
	DONE;
}
