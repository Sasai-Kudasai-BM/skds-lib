package net.skds.lib2.io.json;

import lombok.CustomLog;
import net.skds.lib2.io.chars.CharOutput;
import net.skds.lib2.io.codec.UniversalWriter;
import net.skds.lib2.io.exception.EndOfOutputException;
import net.skds.lib2.utils.StringUtils;
import net.skds.lib2.utils.exception.StackUnderflowException;

import java.io.IOException;
import java.util.UUID;

@CustomLog
public final class FlatJsonWriterImpl implements UniversalWriter {

	private final CharOutput output;

	private StackEntry stack;

	public FlatJsonWriterImpl(CharOutput output) {
		this.output = output;
	}

	@Override
	public void beginObject() throws IOException {
		pushStack(false);
		output.append('{');
	}

	@Override
	public void endObject() throws IOException {
		popStack();
		output.append('}');
	}

	@Override
	public void beginList() throws IOException {
		pushStack(true);
		output.append('[');
	}

	@Override
	public void endList() throws IOException {
		popStack();
		output.append(']');
	}

	@Override
	public void writeName(String name) throws IOException {
		pushName();
		StringUtils.writeQuoted(output, name, '"');
		output.append(':');
	}

	@Override
	public void writeString(String s) throws IOException {
		pushValue();
		StringUtils.writeQuoted(output, s, '"');
	}

	@Override
	public void writeRaw(String s) throws IOException {
		pushValue();
		output.append(s);
	}

	@Override
	public void writeBoolean(boolean b) throws IOException {
		pushValue();
		output.append(String.valueOf(b));
	}

	@Override
	public void writeNull() throws IOException {
		pushValue();
		output.append("null");
	}

	@Override
	public void writeLong(long n) throws IOException {
		pushValue();
		output.append(String.valueOf(n));
	}

	//@Override
	//public void writeTime(long n) throws IOException {
	//	pushValue();
	//	output.append(String.valueOf(n));
	//}

	@Override
	public void writeInt(int n) throws IOException {
		pushValue();
		output.append(String.valueOf(n));
	}

	@Override
	public void writeShort(short n) throws IOException {
		pushValue();
		output.append(String.valueOf(n));
	}

	@Override
	public void writeByte(byte n) throws IOException {
		pushValue();
		output.append(String.valueOf(n));
	}

	@Override
	public void writeHex(long n) throws IOException {
		writeLong(n);
		//throw new UnsupportedOperationException("Hex ints are not available in " + capabilityVersion());
	}

	@Override
	public void writeHex(int n) throws IOException {
		writeInt(n);
		//throw new UnsupportedOperationException("Hex ints are not available in " + capabilityVersion());
	}

	@Override
	public void writeDouble(double n) throws IOException {
		pushValue();
		output.append(String.valueOf(n));
	}

	@Override
	public void writeFloat(float n) throws IOException {
		pushValue();
		output.append(String.valueOf(n));
	}

	@Override
	public void writeDoubleExp(double n) throws IOException {
		writeDouble(n);
		//throw new UnsupportedOperationException("Exponents are not available in " + capabilityVersion());
	}

	@Override
	public void writeFloatExp(float n) throws IOException {
		writeFloat(n);
		//throw new UnsupportedOperationException("Exponents are not available in " + capabilityVersion());
	}

	@Override
	public void writeByteArray(byte[] b) throws IOException {
		beginList();
		for (int i = 0; i < b.length; i++) {
			writeInt(b[i]);
		}
		endList();
	}

	@Override
	public void writeCharArray(char[] arr) throws IOException {
		beginList();
		for (int i = 0; i < arr.length; i++) {
			writeString(StringUtils.unicodeCharUC(arr[i]));
		}
		endList();
	}

	@Override
	public void writeShortArray(short[] arr) throws IOException {
		beginList();
		for (int i = 0; i < arr.length; i++) {
			writeInt(arr[i]);
		}
		endList();
	}

	@Override
	public void writeIntArray(int[] arr) throws IOException {
		beginList();
		for (int i = 0; i < arr.length; i++) {
			writeInt(arr[i]);
		}
		endList();
	}

	@Override
	public void writeLongArray(long[] arr) throws IOException {
		beginList();
		for (int i = 0; i < arr.length; i++) {
			writeLong(arr[i]);
		}
		endList();
	}

	@Override
	public void writeFloatArray(float[] arr) throws IOException {
		beginList();
		for (int i = 0; i < arr.length; i++) {
			writeFloat(arr[i]);
		}
		endList();
	}

	@Override
	public void writeDoubleArray(double[] arr) throws IOException {
		beginList();
		for (int i = 0; i < arr.length; i++) {
			writeDouble(arr[i]);
		}
		endList();
	}

	@Override
	public void writeUUID(UUID uuid) throws IOException {
		writeString(uuid.toString());
	}

	@Override
	public void writeComment(String comment) {
		//throw new UnsupportedOperationException("Comments are not available in " + capabilityVersion());
	}

	@Override
	public void lineBreakEnable(boolean separate) throws EndOfOutputException {
	}

	private void pushName() throws IOException {
		StackEntry e = this.stack;
		if (e == null) throw new StackUnderflowException();
		if (!e.isList) {
			if (e.n++ > 0) {
				output.append(',');
			}
		}
	}

	private void pushValue() throws IOException {
		StackEntry e = this.stack;
		if (e == null && this.output.getPos() == 0) {
			this.stack = new StackEntry(null, false);
			return;
		}
		if (e == null) throw new StackUnderflowException();
		if (e.isList) {
			if (e.n++ > 0) {
				output.append(',');
			}
		}
	}

	private void pushStack(boolean isList) throws IOException {
		if (this.stack != null) pushValue();
		this.stack = new StackEntry(this.stack, isList);
	}

	private void popStack() {
		StackEntry e = this.stack;
		if (e == null) throw new StackUnderflowException();
		this.stack = e.parent;
	}

	private static class StackEntry {
		int n;
		final StackEntry parent;
		final boolean isList;

		private StackEntry(StackEntry parent, boolean isList) {
			this.parent = parent;
			this.isList = isList;
		}
	}
}
