package net.skds.lib2.io;

public class StringCharInput implements CharInput {

	private final String value;
	private int pos;

	public StringCharInput(String value) {
		this.value = value;
	}
	
	@Override
	public int getPos() {
		return pos;
	}

	@Override
	public void setPos(int newPos) {
		this.pos = newPos;
	}

	@Override
	public void skip(int n) {
		this.pos += n;
	}

	@Override
	public boolean isAvailable(int indexFrom, int count) {
		return indexFrom + count < value.length();
	}

	@Override
	public boolean isAvailable(int count) {
		return pos + count < value.length();
	}

	@Override
	public char getCharAt(int index) throws EndOfInputException {
		try {
			return value.charAt(index);
		} catch (IndexOutOfBoundsException e) {
			throw new EndOfInputException(e.getMessage());
		}
	}

	@Override
	public char getCurrentChar() throws EndOfInputException {
		try {
			return value.charAt(pos);
		} catch (IndexOutOfBoundsException e) {
			throw new EndOfInputException(e.getMessage());
		}
	}

	@Override
	public char getCurrentCharAntInc() throws EndOfInputException {
		try {
			return value.charAt(pos++);
		} catch (IndexOutOfBoundsException e) {
			throw new EndOfInputException(e.getMessage());
		}
	}

	@Override
	public char getNextChar() throws EndOfInputException {
		try {
			return value.charAt(++pos);
		} catch (IndexOutOfBoundsException e) {
			throw new EndOfInputException(e.getMessage());
		}
	}

	@Override
	public String subString(int indexFrom, int indexTo) throws EndOfInputException {
		try {
			return value.substring(indexFrom, indexTo);
		} catch (IndexOutOfBoundsException e) {
			throw new EndOfInputException(e.getMessage());
		}
	}

	@Override
	public String subString(int length) throws EndOfInputException {
		try {
			return value.substring(pos, pos + length);
		} catch (IndexOutOfBoundsException e) {
			throw new EndOfInputException(e.getMessage());
		}
	}

	@Override
	public CharSequence getChars(int indexFrom, int count) throws EndOfInputException {
		if (indexFrom + count > value.length()) {
			throw new EndOfInputException("Length required " + (indexFrom + count) + " but limit is " + value.length());
		}
		return new SubSequence(count, indexFrom, value);
	}

	@Override
	public CharSequence getChars(int length) throws EndOfInputException {
		if (pos + length > value.length()) {
			throw new EndOfInputException("Length required " + (pos + length) + " but limit is " + value.length());
		}
		return new SubSequence(length, pos, value);
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
