package net.skds.lib2.network.intercom;

import net.skds.lib2.network.AddressLocation;
import net.skds.lib2.network.intercom.packet.HandshakeStatus;
import net.skds.lib2.network.intercom.packet.system.*;
import net.skds.lib2.network.tcp.TCPConnectionOptions;
import net.skds.lib2.security.AuthorizationData;
import net.skds.lib2.utils.SKDSUtils;

import java.nio.channels.SocketChannel;

public abstract class ServerIntercomConnection extends IntercomConnection<ServerIntercomConnection> {


	public ServerIntercomConnection(SocketChannel channel,
									IntercomConnectionOwner<ServerIntercomConnection, ?> connectionOwner,
									IntercomSettings intercomSettings,
									TCPConnectionOptions options
	) {
		super(channel, true, connectionOwner, intercomSettings, options);
	}

	public final void onPublicInfoRequest() {
		send(new PublicInfoResponseS2CPacket(getPublicInfo()));
	}

	protected abstract byte[] getCertificates();

	public void onEncryptionSuccess(EncryptionSuccessC2SPacket packet) {
		boolean checkCert = getSecurityLevel().checkCertificate;
		if (checkCert) {
			validateHandshakeStatus(HandshakeStatus.ENCRYPTION, HandshakeStatus.CERT_VALIDATION, packet);
		} else {
			validateHandshakeStatus(HandshakeStatus.ENCRYPTION, HandshakeStatus.AUTHORIZATION, packet);
			// wait for authorization
		}
		((ServerIntercomEncryption) this.encryption).applySecret(packet.getKey());
		enableEncryption();
		if (checkCert) {
			send(new CertificateS2CPacket(getCertificates()));
		}
	}

	public void handshakeStart(HandshakeStartC2SPacket packet) {
		validateHandshakeStatus(HandshakeStatus.NONE, HandshakeStatus.HANDSHAKE, packet);
		validateSecurityLevel(packet.getSecurityLevel());
		receivePublicInfo(packet.getPublicInfo());
		setSecurityLevel(packet.getSecurityLevel());
		if (packet.getSecurityLevel().useEncryption) {
			ServerIntercomEncryption e = new ServerIntercomEncryption();
			this.encryption = e;
			e.createPublicRSAKey().thenAccept(k -> {
				validateHandshakeStatus(HandshakeStatus.HANDSHAKE, HandshakeStatus.ENCRYPTION);
				send(new EncryptionStartS2CPacket(k.getEncoded()));
			}).exceptionally(SKDSUtils.getCatcher());
		} else {
			validateHandshakeStatus(HandshakeStatus.HANDSHAKE, HandshakeStatus.AUTHORIZATION, packet);
			send(new HandshakeSuccessS2CPacket());
		}
	}

	public void validateSecurityLevel(SecurityLevel securityLevel) {
		SecurityOptions secOps = getConnectionOwner().getSecurityOptions();
		if (secOps.minimumSecurityLevel().greater(securityLevel)) {
			throw new SecurityException("Insufficient security level %s, minimum is %s".formatted(securityLevel, secOps.minimumSecurityLevel()));
		}
		if (secOps.forceEncryption() && !securityLevel.useEncryption) {
			throw new SecurityException("Encryption required");
		}
		if (secOps.forceCertificate() && !securityLevel.checkCertificate) {
			throw new SecurityException("Certificate required");
		}
		switch (securityLevel) {
			case LOCALHOST -> {
				if (getAddressLocation() != AddressLocation.LOCALHOST) throw insufficientForAddress(securityLevel);
			}
			case LAN_NO_ENCRYPTION, LAN_SAFE -> {
				if (getAddressLocation().greater(AddressLocation.LAN)) throw insufficientForAddress(securityLevel);
			}
		}
	}

	public final void authorizationStart(AuthorizationData authorizationData) {
		String err = processAuthorization(authorizationData);
		if (err == null) {
			validateHandshakeStatus(HandshakeStatus.AUTHORIZATION, HandshakeStatus.DONE);
			setAuthorized();
			onAuthorizationSuccess(authorizationData);
		}
		send(new AuthorizationResultS2CPacket(err));
	}

	protected abstract String processAuthorization(AuthorizationData authorizationData);

	protected abstract void onAuthorizationSuccess(AuthorizationData authorizationData);

	private SecurityException insufficientForAddress(SecurityLevel level) {
		return new SecurityException("Security level " + level + " is insufficient for address " + getAddress());
	}
}
