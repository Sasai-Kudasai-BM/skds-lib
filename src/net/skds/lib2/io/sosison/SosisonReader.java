package net.skds.lib2.io.sosison;

import net.skds.lib2.io.ExtendedDataInput;
import net.skds.lib2.io.codec.UniversalReader;
import net.skds.lib2.io.exception.EndOfInputException;
import net.skds.lib2.io.exception.ParseException;
import net.skds.lib2.mat.VarInt;
import net.skds.lib2.utils.Numbers;
import net.skds.lib2.utils.exception.StackUnderflowException;

import java.io.IOException;
import java.util.UUID;

public final class SosisonReader implements UniversalReader {

	private static final byte BOOLEAN_MASK = ~127;

	private final ExtendedDataInput input;

	private int depth = 0;
	private int nextID;
	private String nextName;
	private boolean nextIsArray = false;
	private SosisonEntryType next = null;
	//private boolean read = false;

	public SosisonReader(ExtendedDataInput input) {
		this.input = input;
	}

	private SosisonEntryType readNext() throws IOException {
		if (this.depth == -1) {
			throw new EndOfInputException("No more data in this object");
		}
		//this.read = false;
		int id = input.readByte();
		SosisonEntryType n = SosisonEntryType.byID(id);
		nextIsArray = n.supportArrays() && (id & SosisonEntryType.ARRAY_FLAG) != 0;
		this.next = n;
		this.nextID = id;
		if (!n.isTerminal()) {
			nextName = input.readSizedString();
		}
		return n;
	}

	private void pushDepth() {
		//this.read = true;
		this.depth++;
	}

	private void popDepth() {
		if (this.depth > 0) {
			this.depth--;
		} else throw new StackUnderflowException();
	}


	private void validateEntryType(SosisonEntryType expected) throws IOException {
		SosisonEntryType next = this.nextEntryType();
		if (next != expected) {
			throw new ParseException("Expected " + expected + " but next entry is " + next);
		}
	}

	@Override
	public SosisonEntryType nextEntryType() throws IOException {
		SosisonEntryType n = this.next;
		if (n == null) {
			n = readNext();
		}
		return n;
	}


	@Override
	public String readName() throws IOException {
		return nextName;
	}

	@Override
	public void beginObject() throws IOException {
		validateEntryType(SosisonEntryType.BEGIN_OBJECT);
		pushDepth();
	}

	@Override
	public void endObject() throws IOException {
		validateEntryType(SosisonEntryType.END_OBJECT);
		popDepth();
	}

	@Override
	public void beginList() throws IOException {
		validateEntryType(SosisonEntryType.BEGIN_LIST);
		pushDepth();
	}

	@Override
	public void endList() throws IOException {
		validateEntryType(SosisonEntryType.END_LIST);
		popDepth();
	}

	@Override
	public boolean readBoolean() throws IOException {
		validateEntryType(SosisonEntryType.BOOLEAN);
		return (nextID & BOOLEAN_MASK) != 0;
	}

	@Override
	public void skipNull() {
		//this.read = true;
	}

	@Override
	public void skipValue() {
		//this.read = true;
	}

	@Override
	public Number readNumber() throws IOException {
		Number n;
		switch (nextEntryType()) {
			case INT -> n = readInt();
			case LONG -> n = readLong();
			//case TIME -> n = readTime();
			case FLOAT -> n = readFloat();
			case DOUBLE -> n = readDouble();
			case STRING -> n = Numbers.parseNumber(readString());
			default -> throw new ParseException("Next entry is not a number");
		}
		return n;
	}

	@Override
	public byte readByte() throws IOException {
		validateEntryType(SosisonEntryType.INT);
		return input.readByte();
	}

	@Override
	public short readShort() throws IOException {
		validateEntryType(SosisonEntryType.INT);
		return input.readShort();
	}

	@Override
	public int readInt() throws IOException {
		validateEntryType(SosisonEntryType.INT);
		return input.readInt();
	}

	@Override
	public long readLong() throws IOException {
		validateEntryType(SosisonEntryType.LONG);
		return input.readLong();
	}

	@Override
	public float readFloat() throws IOException {
		validateEntryType(SosisonEntryType.FLOAT);
		return input.readFloat();
	}

	@Override
	public double readDouble() throws IOException {
		validateEntryType(SosisonEntryType.DOUBLE);
		return input.readDouble();
	}

	//@Override
	//public long readTime() throws IOException {
	//	validateEntryType(SosisonEntryType.TIME);
	//	return input.readLong();
	//}

	@Override
	public String readString() throws IOException {
		validateEntryType(SosisonEntryType.STRING);
		// TODO
		return "StringUtils.readSizedString(input)";
	}

	@Override
	public byte[] readByteArray() throws IOException {
		//validateEntryType(SosisonEntryType.BINARY);
		int l = VarInt.read(input);
		byte[] data = new byte[l];
		input.readFully(data);
		return data;
	}

	@Override
	public short[] readShortArray() throws IOException {
		// TODO
		return new short[0];
	}

	@Override
	public char[] readCharArray() throws IOException {
		// TODO
		return new char[0];
	}

	@Override
	public int[] readIntArray() throws IOException {
		// TODO
		return new int[0];
	}

	@Override
	public long[] readLongArray() throws IOException {
		// TODO
		return new long[0];
	}

	@Override
	public float[] readFloatArray() throws IOException {
		// TODO
		return new float[0];
	}

	@Override
	public double[] readDoubleArray() throws IOException {
		// TODO
		return new double[0];
	}

	@Override
	public UUID readUUID() throws IOException {
		validateEntryType(SosisonEntryType.UUID);
		return null;
	}

}
