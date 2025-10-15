package net.skds.lib2.network.intercom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;

public class ClientIntercomEncryption extends IntercomEncryption {

	private static final KeyGenerator generator;
	private static final KeyFactory keyFactory;

	public byte[] createSecret(byte[] rsaSecret) {
		try {

			PublicKey key = keyFactory.generatePublic(new X509EncodedKeySpec(rsaSecret, "RSA"));
			Cipher secretEncryptor = Cipher.getInstance("RSA");
			secretEncryptor.init(Cipher.DECRYPT_MODE, key);

			SecretKey aesKey = generator.generateKey();
			IvParameterSpec iv = new IvParameterSpec(aesKey.getEncoded());
			this.encryptor = Cipher.getInstance("AES/CFB8/NoPadding");
			encryptor.init(Cipher.ENCRYPT_MODE, aesKey, iv);
			this.decryptor = Cipher.getInstance("AES/CFB8/NoPadding");
			decryptor.init(Cipher.DECRYPT_MODE, aesKey, iv);

			return secretEncryptor.doFinal(aesKey.getEncoded());

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	static {
		try {
			generator = KeyGenerator.getInstance("AES");
			keyFactory = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		generator.init(256, new SecureRandom());
	}
}
