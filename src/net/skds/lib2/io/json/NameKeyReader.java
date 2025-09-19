package net.skds.lib2.io.json;

import net.skds.lib2.io.codec.UniversalReader;
import net.skds.lib2.io.sosison.SosisonEntryType;
import net.skds.lib2.utils.Numbers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

public final class NameKeyReader implements UniversalReader {

	private final String input;

	private boolean read;

	public NameKeyReader(String input) {
		this.input = input;
	}

	@Override
	public String readName() throws IOException {
		throw unsupported();
	}

	private void validateReadability() {
		if (read) throw new IllegalStateException("already read");
		read = true;
	}

	private UnsupportedEncodingException unsupported() {
		return new UnsupportedEncodingException("This reader is only for key parsing");
	}

	@Override
	public String readString() throws IOException {
		validateReadability();
		return input;
	}


	@Override
	public UUID readUUID() throws IOException {
		return UUID.fromString(readString());
	}

	@Override
	public Number readNumber() throws IOException {
		return Numbers.parseNumber(readString());
	}

	@Override
	public void beginObject() throws IOException {
		throw unsupported();
	}

	@Override
	public void endObject() throws IOException {
		throw unsupported();
	}

	@Override
	public void beginList() throws IOException {
		throw unsupported();
	}

	@Override
	public void endList() throws IOException {
		throw unsupported();
	}

	@Override
	public void skipNull() throws IOException {
		throw unsupported();
	}

	@Override
	public void skipValue() {
		validateReadability();
	}

	@Override
	public boolean readBoolean() throws IOException {
		return Boolean.parseBoolean(readString());
	}

	@Override
	public SosisonEntryType nextEntryType() throws IOException {
		return SosisonEntryType.STRING;
	}

}
