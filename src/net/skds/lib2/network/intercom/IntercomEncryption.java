package net.skds.lib2.network.intercom;

import lombok.Getter;
import net.skds.lib2.security.CipherInputStream;
import net.skds.lib2.security.CipherOutputStream;

import javax.crypto.Cipher;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class IntercomEncryption {

	@Getter
	protected Cipher encryptor;
	@Getter
	protected Cipher decryptor;


	public InputStream wrapInput(InputStream in) {
		return new CipherInputStream(in, decryptor);
	}

	public OutputStream wrapOutput(OutputStream out) {
		return new CipherOutputStream(out, encryptor);
	}
}
