package net.skds.lib2.io;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamCharInput extends StringCharInput {

	public InputStreamCharInput(InputStream stream) throws IOException {
		super(String.valueOf(stream.readAllBytes()));
	}
}