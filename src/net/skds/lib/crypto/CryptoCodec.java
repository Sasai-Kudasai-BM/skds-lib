package net.skds.lib.crypto;

import net.skds.lib.utils.linkiges.Pair;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class CryptoCodec {

	private final Cipher encryptor;
	private final Cipher decryptor;

	private static final KeyPairGenerator generator;

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

	private CryptoCodec(Cipher encryptor, Cipher decryptor) {
		this.encryptor = encryptor;
		this.decryptor = decryptor;
	}

	public static CryptoCodec createAES(String key) {
		return new CryptoCodec(key.getBytes(StandardCharsets.UTF_8), "AES");
	}

	public static Pair<CryptoCodec, byte[]> createHostRSA() {
		KeyPair kp = generator.generateKeyPair();
		try {
			Cipher decryptor = Cipher.getInstance("RSA");
			decryptor.init(Cipher.DECRYPT_MODE, kp.getPrivate());
			return new Pair<>(new CryptoCodec(null, decryptor), kp.getPublic().getEncoded());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static CryptoCodec createClientRSA(byte[] publicKey) {
		SecretKey key = new SecretKeySpec(publicKey, 0, publicKey.length, "RSA");
		try {
			Cipher encryptor = Cipher.getInstance("RSA");
			encryptor.init(Cipher.ENCRYPT_MODE, key);
			return new CryptoCodec(encryptor, null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public byte[] encrypt(byte[] data) {
		if (encryptor == null) throw new UnsupportedOperationException("Decrypt only");
		try {
			synchronized (encryptor) {
				return encryptor.doFinal(data);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public byte[] encrypt(ByteBuffer data) {
		if (encryptor == null) throw new UnsupportedOperationException("Decrypt only");
		try {
			synchronized (encryptor) {
				return encryptor.doFinal(data.array(), data.position(), data.remaining());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public byte[] decrypt(byte[] data) {
		if (decryptor == null) throw new UnsupportedOperationException("Encrypt only");
		try {
			synchronized (decryptor) {
				return decryptor.doFinal(data);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public byte[] decrypt(ByteBuffer data) {
		if (decryptor == null) throw new UnsupportedOperationException("Encrypt only");
		try {
			synchronized (decryptor) {
				return decryptor.doFinal(data.array(), data.position(), data.remaining());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	static {
		try {
			generator = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		generator.initialize(2048);
	}
}
