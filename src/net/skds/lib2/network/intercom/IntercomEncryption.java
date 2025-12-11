package net.skds.lib2.network.intercom;

import lombok.Getter;
import net.skds.lib2.utils.exception.TODOException;

import javax.crypto.Cipher;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class IntercomEncryption {

	@Getter
	protected Cipher encryptor;
	@Getter
	protected Cipher decryptor;


	//public void encrypt(byte[] buffer, int from, int count) {
	//	byte[] data = encryptor.update(buffer, from, count);
	//	System.arraycopy(data, 0, buffer, from, count);
	//}

	//public void decrypt(byte[] buffer, int from, int count) {
	//	byte[] data = decryptor.update(buffer, from, count);
	//	System.arraycopy(data, 0, buffer, from, count);
	//}

	public InputStream wrapInput(InputStream in) {
		//return new CipherInputStream(in, decryptor);
		throw new TODOException();
	}

	public OutputStream wrapOutput(OutputStream out) {
		//return new CipherOutputStream(out, encryptor);
		throw new TODOException();
	}
}
