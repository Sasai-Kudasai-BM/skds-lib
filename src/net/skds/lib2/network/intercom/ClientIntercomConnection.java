package net.skds.lib2.network.intercom;

import net.skds.lib2.annotations.Nullable;
import net.skds.lib2.network.intercom.packet.HandshakeStatus;
import net.skds.lib2.network.intercom.packet.system.*;
import net.skds.lib2.network.tcp.TCPConnectionOptions;
import net.skds.lib2.security.AuthorizationData;
import net.skds.lib2.security.CertificateStatus;
import net.skds.lib2.security.X509Utils;
import net.skds.lib2.utils.logger.SKDSLogger;

import java.io.ByteArrayInputStream;
import java.nio.channels.SocketChannel;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public abstract class ClientIntercomConnection extends IntercomConnection<ClientIntercomConnection> {


	public ClientIntercomConnection(SocketChannel channel,
									IntercomClient client,
									IntercomSettings intercomSettings,
									TCPConnectionOptions options
	) {
		super(channel, false, client, intercomSettings, options);
	}

	public IntercomClient getClient() {
		return (IntercomClient) getConnectionOwner();
	}

	public void startHandshake(@Nullable SecurityLevel securityLevel) {
		validateHandshakeStatus(HandshakeStatus.NONE, HandshakeStatus.HANDSHAKE);
		if (securityLevel == null) {
			securityLevel = SecurityLevel.getOptimal(getAddressLocation());
		}
		setSecurityLevel(securityLevel);
		send(new HandshakeStartC2SPacket(getConnectionOwner().getName(), getPublicInfo(), System.currentTimeMillis(), securityLevel));
	}

	public void handshakeSuccess() {
		if (getSecurityLevel().checkCertificate) {
			validateHandshakeStatus(HandshakeStatus.HANDSHAKE, HandshakeStatus.CERT_VALIDATION);
			// wait for certificate
		} else {
			validateHandshakeStatus(HandshakeStatus.HANDSHAKE, HandshakeStatus.AUTHORIZATION);
			send(new AuthorizationStartC2SPacket(getAuthorizationData()));
		}
	}

	public void encryptionStart(EncryptionStartS2CPacket packet) {
		validateHandshakeStatus(HandshakeStatus.HANDSHAKE, HandshakeStatus.ENCRYPTION);

		ClientIntercomEncryption e = new ClientIntercomEncryption();
		this.encryption = e;
		byte[] encryptedAESKey = e.createSecret(packet.getPublicKey());

		boolean checkCert = getSecurityLevel().checkCertificate;
		if (checkCert) {
			validateHandshakeStatus(HandshakeStatus.ENCRYPTION, HandshakeStatus.CERT_VALIDATION);
		} else {
			validateHandshakeStatus(HandshakeStatus.ENCRYPTION, HandshakeStatus.AUTHORIZATION);
		}
		send(new EncryptionSuccessC2SPacket(encryptedAESKey));
		enableEncryption();

		if (!checkCert) {
			send(new AuthorizationStartC2SPacket(getAuthorizationData()));
		}
	}

	public final void receiveCertificate(byte[] certData) {
		validateHandshakeStatus(HandshakeStatus.CERT_VALIDATION);
		X509Certificate[] certificates = X509Utils.readCertificates(new ByteArrayInputStream(certData));
		CertificateStatus status = CertificateStatus.INVALID;
		try {
			status = getClient().getTrustManager().checkCertChain(certificates, getAddress().getHostName());
		} catch (CertificateException e) {
			e.printStackTrace(SKDSLogger.WARN_PRINTSTREAM);
		}
		if (status.isOk()) {
			validateHandshakeStatus(HandshakeStatus.CERT_VALIDATION, HandshakeStatus.AUTHORIZATION);
			onCertificateValidated();
			send(new AuthorizationStartC2SPacket(getAuthorizationData()));
		} else {
			disconnect("Certificate status: " + status);
		}
	}

	public final void onAuthorize(String error) {
		if (error == null) {
			validateHandshakeStatus(HandshakeStatus.AUTHORIZATION, HandshakeStatus.DONE);
			setAuthorized();
			onAuthorizationSuccess();
		} else {
			onAuthorizationFail(error);
		}
	}

	public void requestPublicInfo() {
		send(new PublicInfoRequestC2SPacket());
	}

	protected abstract AuthorizationData getAuthorizationData();

	protected abstract void onAuthorizationSuccess();

	protected abstract void onAuthorizationFail(String error);

	protected abstract void onCertificateValidated();

}
