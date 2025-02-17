package net.skds.lib2.utils.linkiges;

public record Obj2IntPairRecord<O>(O objectValue, int intValue) implements Obj2IntPair<O> {
	public Obj2IntPairRecord(int floatValue, O objectValue) {
		this(objectValue, floatValue);
	}
}
