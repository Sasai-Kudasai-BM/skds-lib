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
	private final JsonCapabilityVersion cpv;

	private StackEntry stack;
	private String nextComment;

	public FormattedJsonWriterImpl(CharOutput output, String tab, JsonCapabilityVersion cpv) {
		this.output = output;
		this.tab = tab;
		this.cpv = cpv;
	}

	@Override
	public JsonCapabilityVersion capabilityVersion() {
		return cpv;
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
	public void writeHex(long n) throws IOException {
		if (cpv == JsonCapabilityVersion.JSON5) {
			pushValue();
			output.append(StringUtils.hexIntUC(n));
		} else {
			writeInt(n);
		}
	}

	@Override
	public void writeFloat(double n) throws IOException {
		pushValue();
		output.append(String.valueOf(n));
	}

	@Override
	public void writeFloatExp(double n) throws IOException {
		if (cpv == JsonCapabilityVersion.JSON5) {
			pushValue();
			output.append(StringUtils.expFloatUC(n));
		} else {
			writeFloat(n);
		}
	}

	@Override
	public void writeComment(String comment) {
		if (cpv != JsonCapabilityVersion.JSON) {
			nextComment = comment;
		}
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
			String nc = nextComment;
			if (nc != null) {
				e.lineBreak = true;
			}
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
			if (nc != null) {
				writeComment0(nc);
			}
		}
	}

	private void pushValue() throws IOException {
		StackEntry e = this.stack;
		if (e == null) throw new StackUnderflowException();
		if (e.isList) {
			String nc = nextComment;
			if (nc != null) {
				e.lineBreak = true;
			}
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
			if (nc != null) {
				writeComment0(nc);
			}
		}
	}

	private void writeComment0(String nc) throws IOException {
		//output.append('\n');
		if (nc.indexOf('\n') == -1) {
			output.append("// ");
			output.append(nc);
			output.append('\n');
		} else {
			String[] com = nc.split("\n");
			if (com.length > 0) {
				output.append("/* ");
				output.append(com[0]);
				for (int i = 1; i < com.length; i++) {
					output.append('\n');
					writeTabs(stack);
					output.append(com[i]);
				}
				output.append(" */\n");
			}
		}
		writeTabs(stack);
		nextComment = null;
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
