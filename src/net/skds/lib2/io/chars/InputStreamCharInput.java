package net.skds.lib2.io.chars;

import java.io.IOException;
import java.io.InputStream;

//TODO WIP
public class InputStreamCharInput extends StringCharInput {

	public InputStreamCharInput(InputStream stream) throws IOException {
		super(new String(stream.readAllBytes()));
	}
}