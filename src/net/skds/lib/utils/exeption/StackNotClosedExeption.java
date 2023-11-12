package net.skds.lib.utils.exeption;

public class StackNotClosedExeption extends RuntimeException {

	public StackNotClosedExeption() {

	}

	public StackNotClosedExeption(String err) {
		super(err);
	}
}
