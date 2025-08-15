package net.skds.tests;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;

public class ConsoleTest {

	public static void main(String[] args) {

		try {
			byte[] buffer = new byte[64];
			ByteBuffer wrap = ByteBuffer.wrap(buffer);
			CharBuffer out = CharBuffer.allocate(64);

			CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();

			while (true) {
				wrap.clear();
				out.clear();
				int r = System.in.read(buffer);
				wrap.limit(r);

				CoderResult result = decoder.decode(wrap, out, false);

				out.flip();


				System.out.println(result);
				System.out.println(wrap.remaining());
				System.out.println(out.toString());
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
			
	}
}
