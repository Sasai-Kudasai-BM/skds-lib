package net.skds.lib2.io.chars;

public class StringCharOutput implements CharOutput {

	private final StringBuilder builder = new StringBuilder();

	@Override
	public int getPos() {
		return builder.length();
	}

	@Override
	public void append(char c) {
		builder.append(c);
	}

	@Override
	public void append(String string) {
		builder.append(string);
	}

	@Override
	public void append(CharSequence charSequence) {
		builder.append(charSequence);
	}

	@Override
	public String toString() {
		return builder.toString();
	}
}
