package net.skds.lib2.io;

public interface CharInput {

	int getPos();

	void setPos(int newPos);

	void skip(int n);

	boolean isAvailable(int indexFrom, int count);

	boolean isAvailable(int count);

	char getCharAt(int index) throws EndOfInputException;

	char getCurrentChar() throws EndOfInputException;

	char getCurrentCharAntInc() throws EndOfInputException;

	char getNextChar() throws EndOfInputException;

	String subString(int indexFrom, int indexTo) throws EndOfInputException;

	String subString(int length) throws EndOfInputException;

	CharSequence getChars(int indexFrom, int count) throws EndOfInputException;

	CharSequence getChars(int length) throws EndOfInputException;
}
