package net.skds.lib2.network.intercom;

import lombok.Getter;
import net.skds.lib2.utils.SKDSUtils;
import net.skds.lib2.utils.ThreadUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.concurrent.CompletableFuture;

@Getter
public class ServerIntercomEncryption extends IntercomEncryption {

	private static final KeyPairGenerator generator;

	public ServerIntercomEncryption() {
	}

	public CompletableFuture<PublicKey> createPublicRSAKey() {
		return CompletableFuture.supplyAsync(() -> {
			KeyPair kp = generator.generateKeyPair();
			try {
				this.decryptor = Cipher.getInstance("RSA");
				this.decryptor.init(Cipher.DECRYPT_MODE, kp.getPrivate());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			return kp.getPublic();
		}, ThreadUtils.EXECUTOR).exceptionally(SKDSUtils.getCatcher());
	}

	public boolean applySecret(byte[] secret) {
		try {
			secret = decryptor.doFinal(secret);
			SecretKey key = new SecretKeySpec(secret, 0, secret.length, "AES");
			IvParameterSpec iv = new IvParameterSpec(secret, 0, 16);
			this.encryptor = Cipher.getInstance("AES/CFB8/NoPadding");
			encryptor.init(Cipher.ENCRYPT_MODE, key, iv);
			this.decryptor = Cipher.getInstance("AES/CFB8/NoPadding");
			decryptor.init(Cipher.DECRYPT_MODE, key, iv);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			return false;
		}
		return true;
	}

	static {
		try {
			generator = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		generator.initialize(1024 * 2);
	}
}
