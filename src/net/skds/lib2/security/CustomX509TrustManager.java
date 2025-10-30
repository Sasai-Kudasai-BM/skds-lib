package net.skds.lib2.security;

import lombok.CustomLog;

import javax.net.ssl.*;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.*;

@CustomLog
public class CustomX509TrustManager extends X509ExtendedTrustManager {

	private final X509ExtendedTrustManager base;
	private final X509Certificate[] trusted;
	private final Map<String, X509Certificate> trustedMap = new HashMap<>();

	public CustomX509TrustManager(X509ExtendedTrustManager base, X509Certificate... trusted) {
		Objects.requireNonNull(base, "X509TrustManager not found");
		this.base = base;
		X509Certificate[] baseCerts = base.getAcceptedIssuers();
		this.trusted = Arrays.copyOf(trusted, trusted.length + baseCerts.length);
		System.arraycopy(baseCerts, 0, this.trusted, trusted.length, baseCerts.length);
		for (X509Certificate cert : trusted) {
			try {
				cert.verify(cert.getPublicKey());
			} catch (GeneralSecurityException e) {
				throw new IllegalArgumentException("Trusted certificate is not self-signed " + cert, e);
			}
			String principial = cert.getSubjectX500Principal().getName();
			trustedMap.put(principial, cert);
		}
	}

	public CustomX509TrustManager(X509Certificate... trusted) {
		X509ExtendedTrustManager base0 = null;
		try {
			TrustManagerFactory tmf = TrustManagerFactory.getInstance("PKIX");
			tmf.init((KeyStore) null);
			for (TrustManager tm : tmf.getTrustManagers()) {
				if (tm instanceof X509ExtendedTrustManager x509e) {
					base0 = x509e;
				}
			}
		} catch (NoSuchAlgorithmException | KeyStoreException e) {
			throw new RuntimeException(e);
		}
		this(base0, trusted);
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return trusted;
	}

	private Map<String, X509Certificate> getCertMap(X509Certificate[] x509Certificates) throws CertificateParsingException {
		Map<String, X509Certificate> map = new HashMap<>();
		for (int i = 0; i < x509Certificates.length; i++) {
			X509Certificate cert = x509Certificates[i];
			String principial = cert.getSubjectX500Principal().getName();
			map.put(principial, cert);

			Collection<List<?>> an = cert.getSubjectAlternativeNames();
			if (an != null) {
				for (List<?> list : an) {
					String name = list.getLast().toString();
					map.put(name, cert);
				}
			}
		}
		return map;
	}

	private CertificateStatus checkCertChain(X509Certificate cert, Map<String, X509Certificate> certMap) throws CertificateException {

		String issuerName = cert.getIssuerX500Principal().getName();
		X509Certificate trustedIssuer = trustedMap.get(issuerName);
		if (trustedIssuer != null) {
			try {
				cert.verify(trustedIssuer.getPublicKey());
			} catch (GeneralSecurityException e) {
				return CertificateStatus.INVALID;
			}
		} else {
			if (issuerName.equals(cert.getSubjectX500Principal().getName())) {
				return CertificateStatus.INVALID;
			}
			X509Certificate issuer = certMap.remove(issuerName);
			if (issuer == null) return CertificateStatus.INVALID;

			try {
				cert.verify(issuer.getPublicKey());
			} catch (GeneralSecurityException e) {
				return CertificateStatus.INVALID;
			}
			CertificateStatus status = checkCertChain(issuer, certMap);
			if (!status.isOk()) return status;
		}

		Date now = new Date();
		if (now.after(cert.getNotAfter())) {
			return CertificateStatus.EXPIRED;
		} else if (now.before(cert.getNotBefore())) {
			return CertificateStatus.NOT_VALID_YET;
		}

		return CertificateStatus.VALID;
	}

	public CertificateStatus checkCertChain(X509Certificate[] x509Certificates, String host) throws CertificateException {
		Map<String, X509Certificate> certMap = getCertMap(x509Certificates);
		X509Certificate cert = certMap.get(host);
		if (cert == null) return CertificateStatus.INVALID;
		return checkCertChain(cert, certMap);
	}

	@Override
	public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
		base.checkServerTrusted(x509Certificates, s);
	}

	@Override
	public void checkServerTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {
		CertificateStatus status = checkCertChain(x509Certificates, socket.getInetAddress().getHostName());
		if (status.isOk()) {
			return;
		}
		try {
			base.checkServerTrusted(x509Certificates, s, socket);
		} catch (CertificateException ce) {
			log.warn("CertificateException status: " + status);
			throw ce;
		}
	}

	@Override
	public void checkServerTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {
		String host = sslEngine.getPeerHost();
		if (sslEngine.getHandshakeSession() instanceof ExtendedSSLSession es) {
			var names = es.getRequestedServerNames();
			if (!names.isEmpty() && names.getFirst() instanceof SNIHostName hn) {
				host = hn.getAsciiName();
			}
		}
		CertificateStatus status = checkCertChain(x509Certificates, host);
		if (status.isOk()) {
			return;
		}
		try {
			base.checkServerTrusted(x509Certificates, s, sslEngine);
		} catch (CertificateException ce) {
			log.warn("CertificateException status: " + status);
			throw ce;
		}
	}


	@Override
	public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
		base.checkClientTrusted(x509Certificates, s);
	}

	@Override
	public void checkClientTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {
		base.checkClientTrusted(x509Certificates, s, socket);
	}

	@Override
	public void checkClientTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {
		base.checkClientTrusted(x509Certificates, s, sslEngine);
	}
}
