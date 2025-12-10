package net.skds.lib2.security;


import lombok.CustomLog;
import net.skds.lib2.utils.ArrayUtils;
import net.skds.lib2.utils.SKDSUtils;

import javax.net.ssl.*;
import java.net.Socket;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.function.Function;

@CustomLog
public class CustomKeyManager extends X509ExtendedKeyManager {
	private final Map<String, X509Credentials> credentialsMap = new HashMap<>();
	private final Map<String, String> aliasMap = new HashMap<>();
	private static final String LOCALHOST = "localhost";

	public CustomKeyManager(KeyStore ks, Function<String, char[]> passwords)
			throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateParsingException {
		Objects.requireNonNull(ks);
		Enumeration<String> aliases = ks.aliases();

		while (aliases.hasMoreElements()) {
			String alias = aliases.nextElement();
			if (ks.isKeyEntry(alias)) {
				PrivateKey key = (PrivateKey) ks.getKey(alias, passwords.apply(alias));
				Certificate[] certs = ks.getCertificateChain(alias);
				if (certs == null) continue;
				X509Certificate[] x509Certs = new X509Certificate[certs.length];
				for (int i = 0; i < certs.length; i++) {

					X509Certificate cert = SKDSUtils.castOrThrow(certs[i], X509Certificate.class, "Not a X509 certificate");
					x509Certs[i] = cert;

					aliasMap.put(cert.getSubjectX500Principal().getName(), alias);
					Collection<List<?>> names = cert.getSubjectAlternativeNames();
					if (names != null) for (List<?> l : names) {
						aliasMap.put(l.getLast().toString(), alias);
					}
				}

				X509Credentials cred = new X509Credentials(key, x509Certs);
				this.credentialsMap.put(alias, cred);
			}
		}
	}

	public static KeyManager[] of(KeyStore ks, Function<String, char[]> passwords) {
		try {
			return new KeyManager[]{new CustomKeyManager(ks, passwords)};
		} catch (KeyStoreException |
				 CertificateParsingException |
				 UnrecoverableKeyException |
				 NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public X509Certificate[] getCertificateChain(String alias, String host) {
		if (alias == null) {
			return null;
		} else {
			X509Credentials cred = this.credentialsMap.get(aliasMap.getOrDefault(host, ""));
			return cred == null ? null : cred.certificates.clone();
		}
	}

	@Override
	public X509Certificate[] getCertificateChain(String alias) {
		if (alias == null) {
			return null;
		} else {
			X509Credentials cred = this.credentialsMap.get(alias);
			return cred == null ? null : cred.certificates.clone();
		}
	}

	@Override
	public PrivateKey getPrivateKey(String alias) {
		if (alias == null) {
			return null;
		} else {
			X509Credentials cred = this.credentialsMap.get(alias);
			return cred == null ? null : cred.privateKey;
		}
	}

	@Override
	public String chooseClientAlias(String[] keyTypes, Principal[] issuers, Socket socket) {
		return null;
	}

	@Override
	public String chooseEngineClientAlias(String[] keyType, Principal[] issuers, SSLEngine engine) {
		return null;
	}

	@Override
	public String chooseEngineServerAlias(String keyType, Principal[] issuers, SSLEngine engine) {
		if (engine.getHandshakeSession() instanceof ExtendedSSLSession es) {
			var names = es.getRequestedServerNames();
			if (!names.isEmpty() && names.getFirst() instanceof SNIHostName hn) {
				String n = hn.getAsciiName();
				String cert = aliasMap.get(n);
				if (cert == null) {
					return LOCALHOST;
				}
				return cert;
			} else return LOCALHOST;
		}
		return this.chooseServerAlias(keyType, issuers, null);
	}

	@Override
	public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
		if (keyType == null) {
			return null;
		} else {
			return socket != null ? aliasMap.get(socket.getInetAddress().getHostName()) : LOCALHOST;
		}
	}

	@Override
	public String[] getClientAliases(String keyType, Principal[] issuers) {
		return ArrayUtils.emptyArray();
	}

	@Override
	public String[] getServerAliases(String keyType, Principal[] issuers) {
		return ArrayUtils.emptyArray();
	}

	private record X509Credentials(PrivateKey privateKey, X509Certificate[] certificates) {
	}
}
