package net.skds.lib2.io.json;

import lombok.CustomLog;
import lombok.Getter;
import net.skds.lib2.io.CharOutput;
import net.skds.lib2.io.EndOfOutputException;
import net.skds.lib2.io.json.codec.JsonCapabilityVersion;
import net.skds.lib2.utils.StringUtils;
import net.skds.lib2.utils.exception.StackUnderflowException;

import java.io.IOException;

@CustomLog
public final class FormattedJsonWriterImpl implements JsonWriter {

	@Getter
	private final CharOutput output;
	private final String tab;

	private StackEntry stack;

	public FormattedJsonWriterImpl(CharOutput output, String tab) {
		this.output = output;
		this.tab = tab;
	}

	@Override
	public JsonCapabilityVersion capabilityVersion() {
		return JsonCapabilityVersion.JSON;
	}

	@Override
	public void print() {
		log.debug(this.output);
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
	public void beginArray() throws IOException {
		pushStack(true);
		output.append('[');
	}

	@Override
	public void endArray() throws IOException {
		popStack();
		output.append(']');
	}

	@Override
	public void writeName(String name) throws IOException {
		pushName();
		output.append(StringUtils.quote(name));
		output.append(": ");
	}

	@Override
	public void writeString(String s) throws IOException {
		pushValue();
		output.append(StringUtils.quote(s));
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
	public void writeInt(long n) throws IOException {
		pushValue();
		output.append(String.valueOf(n));
	}

	@Override
	public void writeHex(long n) {
		throw new UnsupportedOperationException("Hex ints are not available in " + capabilityVersion());
	}

	@Override
	public void writeFloat(double n) throws IOException {
		pushValue();
		output.append(String.valueOf(n));
	}

	@Override
	public void writeFloatExp(double n) {
		throw new UnsupportedOperationException("Exponents are not available in " + capabilityVersion());
	}

	@Override
	public void writeComment(String comment) {
		throw new UnsupportedOperationException("Comments are not available in " + capabilityVersion());
	}

	@Override
	public void lineBreakEnable(boolean lineBreak) {
		StackEntry e = this.stack;
		if (e == null) throw new StackUnderflowException();
		e.lineBreak = lineBreak;
	}

	private void writeTabs(StackEntry e) throws EndOfOutputException {
		if (tab != null && !tab.isEmpty()) {
			while (e != null) {
				if (e.lineBreak) {
					output.append(tab);
				}
				e = e.parent;
			}
		}
	}

	private void pushName() throws IOException {
		StackEntry e = this.stack;
		if (e == null) throw new StackUnderflowException();
		if (!e.isList) {
			boolean lb = e.lineBreak;
			if (e.n++ > 0) {
				if (lb) {
					output.append(',');
				} else {
					output.append(", ");
				}
			}
			if (lb) {
				output.append('\n');
				writeTabs(e);
			}
		}
	}

	private void pushValue() throws IOException {
		StackEntry e = this.stack;
		if (e == null) throw new StackUnderflowException();
		if (e.isList) {
			boolean lb = e.lineBreak;
			if (e.n++ > 0) {
				if (lb) {
					output.append(',');
				} else {
					output.append(", ");
				}
			}
			if (lb) {
				output.append('\n');
				writeTabs(e);
			}
		}
	}

	private void pushStack(boolean isList) throws IOException {
		if (this.stack != null) pushValue();
		this.stack = new StackEntry(this.stack, isList);
	}

	private void popStack() throws EndOfOutputException {
		StackEntry e = this.stack;
		if (e == null) throw new StackUnderflowException();
		if (e.lineBreak) {
			output.append('\n');
			writeTabs(e.parent);
		}
		this.stack = e.parent;
	}

	private static class StackEntry {
		int n;
		final StackEntry parent;
		final boolean isList;
		boolean lineBreak;

		private StackEntry(StackEntry parent, boolean isList) {
			this.parent = parent;
			this.isList = isList;
		}
	}
}
