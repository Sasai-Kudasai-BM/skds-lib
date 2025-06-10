package net.skds.lib2.utils.linkiges;

public record Obj2LongPairRecord<O>(O objectValue, long longValue) implements Obj2LongPair<O> {
	public Obj2LongPairRecord(long longValue, O objectValue) {
		this(objectValue, longValue);
	}
}
