package net.skds.lib2.security;

import lombok.experimental.UtilityClass;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

@UtilityClass
public class SKDSTrusted {

	public static final String TRUSTED_CERTIFICATE = """
			-----BEGIN CERTIFICATE-----
			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
			bXpp0tEADv3rtlVDfTWnW1Ek8RN1GXS03RlPPw==
			-----END CERTIFICATE-----
			""";

	private static X509Certificate x509Certificate;

	public static X509Certificate getTrustedRootCertificate() {
		X509Certificate cert = x509Certificate;
		if (cert == null) {
			synchronized (TRUSTED_CERTIFICATE) {
				cert = x509Certificate;
				if (cert == null) {
					try {
						CertificateFactory cf = CertificateFactory.getInstance("X.509");
						cert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(TRUSTED_CERTIFICATE.getBytes(StandardCharsets.UTF_8)));
						x509Certificate = cert;
					} catch (CertificateException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
		return cert;
	}

	public static CustomX509TrustManager createTrustManager(X509ExtendedTrustManager base) {
		return new CustomX509TrustManager(base, getTrustedRootCertificate());
	}

	public static CustomX509TrustManager createTrustManager() {
		return new CustomX509TrustManager(getTrustedRootCertificate());
	}

	public static void addTrusted() {
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[]{createTrustManager()}, SecureRandom.getInstanceStrong());
			SSLContext.setDefault(sc);
		} catch (KeyManagementException | NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
}
