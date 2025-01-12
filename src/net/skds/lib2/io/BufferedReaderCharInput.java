package net.skds.lib2.io;

import java.io.BufferedReader;
import java.util.stream.Collectors;

public class BufferedReaderCharInput extends StringCharInput {
	public BufferedReaderCharInput(BufferedReader reader) {
		super(reader.lines().collect(Collectors.joining()));
	}
}
