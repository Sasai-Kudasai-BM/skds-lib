package net.skds.lib2.io;

public interface CharInput {

	boolean isAvailable(int indexFrom, int count);

	char getCharAt(int index) throws EndOfInputException;

	String subString(int indexFrom, int indexTo) throws EndOfInputException;

	CharSequence getChars(int indexFrom, int count) throws EndOfInputException;
}
