package net.skds.lib2.io;

public class StringCharInput implements CharInput {

	private final String value;

	public StringCharInput(String value) {
		this.value = value;
	}

	@Override
	public boolean isAvailable(int indexFrom, int count) {
		return indexFrom + count < value.length();
	}

	@Override
	public char getCharAt(int index) throws EndOfInputException {
		return value.charAt(index);
	}

	@Override
	public String subString(int indexFrom, int indexTo) throws EndOfInputException {
		return value.substring(indexFrom, indexTo);
	}

	@Override
	public CharSequence getChars(int indexFrom, int count) throws EndOfInputException {
		return new SubSequence(count, indexFrom, value);
	}

	private record SubSequence(int length, int start, String value) implements CharSequence {

		@Override
		public char charAt(int index) {
			return value.charAt(start + index);
		}

		@Override
		public CharSequence subSequence(int start, int end) {
			return value.subSequence(this.start + start, end);
		}
	}
}
