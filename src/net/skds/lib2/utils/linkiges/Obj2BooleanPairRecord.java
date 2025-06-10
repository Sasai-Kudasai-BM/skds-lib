package net.skds.lib2.utils.linkiges;

public record Obj2BooleanPairRecord<O>(O objectValue, boolean booleanValue) implements Obj2BooleanPair<O> {
	public Obj2BooleanPairRecord(boolean boleanValue, O objectValue) {
		this(objectValue, boleanValue);
	}
}
