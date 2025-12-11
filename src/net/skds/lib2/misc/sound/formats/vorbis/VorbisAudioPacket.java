package net.skds.lib2.misc.sound.formats.vorbis;

import net.skds.lib2.io.codec.CodecRole;
import net.skds.lib2.io.codec.annotation.CodecRoleConstrains;

import java.io.IOException;

@CodecRoleConstrains(CodecRole.SERIALIZE)
public class VorbisAudioPacket implements VorbisReadable {

	private final transient VorbisProcessor processor;
	private final transient VorbisIdentificationHeader identificationHeader;
	private final transient VorbisSetupHeader setupHeader;


	public VorbisAudioPacket(VorbisProcessor processor) {
		this.processor = processor;
		this.identificationHeader = processor.getIdentificationHeader();
		this.setupHeader = processor.getSetupHeader();
	}

	@Override
	public void read(ByteArrayBitInputStream input) throws IOException {
		if (!input.readBit()) throw new VorbisException();
		int modeNumber = input.readInt(VorbisUtils.iLog(setupHeader.getModes().length));
		VorbisMode mode = setupHeader.getModes()[modeNumber];
		int blocksize = mode.isBlockflag() ? identificationHeader.getBlockSize0() : identificationHeader.getBlockSize1();

	}

}
