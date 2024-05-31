package net.skds.lib.crypto;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class CryptoCodec {

	private final Cipher encryptor;
	private final Cipher decryptor;

	public CryptoCodec(byte[] secretKey, String algorithm) {
		SecretKey key = new SecretKeySpec(secretKey, 0, secretKey.length, algorithm);
		try {
			encryptor = Cipher.getInstance(algorithm);
			encryptor.init(Cipher.ENCRYPT_MODE, key);
			decryptor = Cipher.getInstance(algorithm);
			decryptor.init(Cipher.DECRYPT_MODE, key);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static CryptoCodec createAES(String key) {
		return new CryptoCodec(key.getBytes(StandardCharsets.UTF_8), "AES");
	}

	public byte[] encrypt(byte[] data) {
		try {
			synchronized (encryptor) {
				return encryptor.doFinal(data);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public byte[] decrypt(byte[] data) {
		try {
			synchronized (decryptor) {
				return decryptor.doFinal(data);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
