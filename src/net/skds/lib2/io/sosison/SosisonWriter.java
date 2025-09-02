package net.skds.lib2.io.sosison;

import net.skds.lib2.io.ExtendedDataOutput;
import net.skds.lib2.io.codec.UniversalWriter;

import java.io.IOException;
import java.util.UUID;

public final class SosisonWriter implements UniversalWriter {

	private final ExtendedDataOutput output;
	//private final boolean comments;
	private String name = "";

	public SosisonWriter(ExtendedDataOutput output) {
		this.output = output;
		//this.comments = comments;
	}

	private void writeHeader(SosisonEntryType type, int flags) throws IOException {
		output.writeByte(type.packFlags(flags));
		if (!type.isTerminal()) {
			if (name == null) throw new IllegalStateException("Name is not set");
			output.writeSizedString(name);
			name = null;
		}
	}

	private void writeArrayHeader(SosisonEntryType arrayType) throws IOException {
		output.writeByte(SosisonEntryType.BEGIN_LIST.packFlags(SosisonEntryType.ARRAY_FLAG));
		output.writeByte(arrayType.getId());
		if (name == null) throw new IllegalStateException("Name is not set");
		output.writeSizedString(name);
		name = null;
	}

	@Override
	public void writeName(String name) throws IOException {
		if (this.name != null) throw new IllegalStateException("Name is already set");
		this.name = name;
	}

	@Override
	public void beginObject() throws IOException {
		writeHeader(SosisonEntryType.BEGIN_OBJECT, 0);
	}

	@Override
	public void endObject() throws IOException {
		writeHeader(SosisonEntryType.END_OBJECT, 0);
	}

	@Override
	public void beginList() throws IOException {
		writeHeader(SosisonEntryType.BEGIN_LIST, 0);
	}

	@Override
	public void endList() throws IOException {
		writeHeader(SosisonEntryType.END_LIST, 0);
	}

	@Override
	public void writeString(String s) throws IOException {
		writeHeader(SosisonEntryType.STRING, 0);
		output.writeSizedString(s);
	}

	@Override
	public void writeRaw(String s) throws IOException {
		writeHeader(SosisonEntryType.STRING, 0);
		output.writeSizedString(s);
	}

	@Override
	public void writeBoolean(boolean b) throws IOException {
		writeHeader(SosisonEntryType.BOOLEAN, b ? SosisonEntryType.BOOLEAN_FLAG : 0);
	}

	@Override
	public void writeNull() throws IOException {
		writeHeader(SosisonEntryType.NULL, 0);
	}

	@Override
	public void writeLong(long n) throws IOException {
		writeHeader(SosisonEntryType.LONG, 0);
		output.writeLong(n);
	}

	//@Override
	//public void writeTime(long n) throws IOException {
	//	writeHeader(SosisonEntryType.TIME, 0);
	//	output.writeLong(n);
	//}

	@Override
	public void writeInt(int n) throws IOException {
		writeHeader(SosisonEntryType.INT, 0);
		output.writeInt(n);
	}

	@Override
	public void writeShort(short n) throws IOException {
		writeHeader(SosisonEntryType.SHORT, 0);
		output.writeShort(n);
	}

	@Override
	public void writeByte(byte n) throws IOException {
		writeHeader(SosisonEntryType.BYTE, 0);
		output.writeByte(n);
	}

	@Override
	public void writeHex(long n) throws IOException {
		writeHeader(SosisonEntryType.LONG, 0);
		output.writeLong(n);
	}

	@Override
	public void writeHex(int n) throws IOException {
		writeHeader(SosisonEntryType.INT, 0);
		output.writeInt(n);
	}

	@Override
	public void writeDouble(double n) throws IOException {
		writeHeader(SosisonEntryType.DOUBLE, 0);
		output.writeDouble(n);
	}

	@Override
	public void writeFloat(float n) throws IOException {
		writeHeader(SosisonEntryType.FLOAT, 0);
		output.writeFloat(n);
	}

	@Override
	public void writeDoubleExp(double n) throws IOException {
		writeHeader(SosisonEntryType.DOUBLE, 0);
		output.writeDouble(n);
	}


	@Override
	public void writeFloatExp(float n) throws IOException {
		writeHeader(SosisonEntryType.FLOAT, 0);
		output.writeFloat(n);
	}

	@Override
	public void writeByteArray(byte[] b) throws IOException {
		writeArrayHeader(SosisonEntryType.BYTE);
		output.writeByteArray(b);
	}

	@Override
	public void writeCharArray(char[] arr) throws IOException {
		writeArrayHeader(SosisonEntryType.SHORT);
		output.writeCharArray(arr);
	}

	@Override
	public void writeShortArray(short[] arr) throws IOException {
		writeArrayHeader(SosisonEntryType.SHORT);
		output.writeShortArray(arr);
	}

	@Override
	public void writeIntArray(int[] arr) throws IOException {
		writeArrayHeader(SosisonEntryType.INT);
		output.writeIntArray(arr);
	}

	@Override
	public void writeLongArray(long[] arr) throws IOException {
		writeArrayHeader(SosisonEntryType.LONG);
		output.writeLongArray(arr);
	}

	@Override
	public void writeFloatArray(float[] arr) throws IOException {
		writeArrayHeader(SosisonEntryType.FLOAT);
		output.writeFloatArray(arr);
	}

	@Override
	public void writeDoubleArray(double[] arr) throws IOException {
		writeArrayHeader(SosisonEntryType.DOUBLE);
		output.writeDoubleArray(arr);
	}

	@Override
	public void writeUUID(UUID uuid) throws IOException {
		writeHeader(SosisonEntryType.UUID, 0);
		output.writeLong(uuid.getMostSignificantBits());
		output.writeLong(uuid.getLeastSignificantBits());
	}

	@Override
	public void writeComment(String comment) throws IOException {
		//if (comments) {
		//	output.writeByte(SosisonEntryType.STRING.packFlags(SosisonEntryType.COMMENT_FLAG));
		//	output.writeSizedString(comment);
		//}
	}

	@Override
	public void lineBreakEnable(boolean lineBreak) {
	}
}
