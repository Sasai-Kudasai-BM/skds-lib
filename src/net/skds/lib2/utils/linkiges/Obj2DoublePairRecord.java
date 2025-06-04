package net.skds.lib2.utils.linkiges;

public record Obj2DoublePairRecord<O>(O objectValue, double doubleValue) implements Obj2DoublePair<O> {
	public Obj2DoublePairRecord(double doubleValue, O objectValue) {
		this(objectValue, doubleValue);
	}
}
