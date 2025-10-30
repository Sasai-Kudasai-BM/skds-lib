package net.skds.lib2.security;

import lombok.experimental.UtilityClass;
import net.skds.lib2.utils.ArrayUtils;
import net.skds.lib2.utils.SKDSUtils;

import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;

@UtilityClass
public class X509Utils {

	public static final CertificateFactory X509_FACTORY;

	public static X509Certificate[] readCertificates(InputStream is) {
		try {
			Collection<?> certificates = X509_FACTORY.generateCertificates(is);
			X509Certificate[] x509Certificates = new X509Certificate[certificates.size()];
			int i = 0;
			for (Object certificate : certificates) {
				x509Certificates[i] = SKDSUtils.castOrThrow(certificate, X509Certificate.class);
				i++;
			}
			return x509Certificates;
		} catch (CertificateException e) {
			e.printStackTrace(System.err);
			return ArrayUtils.emptyArray();
		}
	}

	public static X509Certificate readCertificate(InputStream is) {
		try {
			return SKDSUtils.castOrThrow(X509_FACTORY.generateCertificate(is), X509Certificate.class);
		} catch (CertificateException e) {
			e.printStackTrace(System.err);
			return null;
		}
	}

	static {
		CertificateFactory factory;
		try {
			factory = CertificateFactory.getInstance("X.509");
		} catch (CertificateException e) {
			e.printStackTrace(System.err);
			factory = null;
		}
		X509_FACTORY = factory;
	}

}
