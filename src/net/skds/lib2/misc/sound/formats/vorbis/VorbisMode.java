package net.skds.lib2.misc.sound.formats.vorbis;

import lombok.AccessLevel;
import lombok.Getter;
import net.skds.lib2.io.codec.CodecRole;
import net.skds.lib2.io.codec.annotation.CodecRoleConstrains;
import net.skds.lib2.io.exception.ParseException;

import java.io.IOException;

@SuppressWarnings("SpellCheckingInspection")
@Getter
@CodecRoleConstrains(CodecRole.SERIALIZE)
public class VorbisMode extends VorbisFloor {

	@Getter(AccessLevel.NONE)
	private final transient VorbisProcessor processor;

	private boolean blockflag;
	private int windowtype;
	private int transformtype;
	private int mapping;

	public VorbisMode(VorbisProcessor processor) {
		this.processor = processor;
	}

	@Override
	public void read(ByteArrayBitInputStream input) throws IOException {
		blockflag = input.readBit();
		windowtype = input.readInt(16);
		transformtype = input.readInt(16);
		mapping = input.readInt(8);

		if (windowtype != 0 || transformtype != 0 || mapping >= processor.getSetupHeader().getMappings().length) {
			throw new ParseException();
		}
	}
}
