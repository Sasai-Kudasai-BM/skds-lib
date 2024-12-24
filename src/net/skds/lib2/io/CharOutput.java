package net.skds.lib2.io;

public interface CharOutput {

	int getPos();


	boolean isAvailable(int count);


	void setCurrentChar(char c) throws EndOfOutputException;

	void append(char c) throws EndOfOutputException;

	void append(String string) throws EndOfOutputException;

	void append(CharSequence charSequence) throws EndOfOutputException;

}
