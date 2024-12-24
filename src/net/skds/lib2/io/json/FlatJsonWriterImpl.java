package net.skds.lib2.io.json;

import net.skds.lib2.io.CharOutput;
import net.skds.lib2.io.json.codec.JsonCapabilityVersion;
import net.skds.lib2.utils.StringUtils;

import java.io.IOException;

public final class FlatJsonWriterImpl implements JsonWriter {

	private final CharOutput output;

	public FlatJsonWriterImpl(CharOutput output) {
		this.output = output;
	}

	@Override
	public JsonCapabilityVersion capabilityVersion() {
		return JsonCapabilityVersion.JSON;
	}

	@Override
	public void beginObject() throws IOException {
		output.append('{');
	}

	@Override
	public void endObject() throws IOException {
		output.append('}');
	}

	@Override
	public void beginArray() throws IOException {
		output.append('[');
	}

	@Override
	public void endArray() throws IOException {
		output.append(']');
	}

	@Override
	public void separate() throws IOException {
		output.append(',');
	}

	@Override
	public void writeName(String name) throws IOException {
		output.append(StringUtils.quote(name));
		output.append(':');
	}

	@Override
	public void writeString(String s) throws IOException {
		output.append(StringUtils.quote(s));
	}

	@Override
	public void writeBoolean(boolean b) throws IOException {
		output.append(String.valueOf(b));
	}

	@Override
	public void writeNull() throws IOException {
		output.append("null");
	}

	@Override
	public void writeInt(long n) throws IOException {
		output.append(String.valueOf(n));
	}

	@Override
	public void writeHex(long n) {
		throw new UnsupportedOperationException("Hex ints are not available in " + capabilityVersion());
	}

	@Override
	public void writeFloat(double n) throws IOException {
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
	public void pushLine() {
	}

	@Override
	public void space() {
	}


}
