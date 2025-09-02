package net.skds.lib2.misc.sound.formats.opus;

import lombok.NoArgsConstructor;
import net.skds.lib2.io.codec.SosisonUtils;
import net.skds.lib2.misc.ogg.OggPage;
import net.skds.lib2.utils.SKDSByteBuf;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@NoArgsConstructor
public class OpusHeader {

	int version;
	int channels;
	int skip;
	int sampleRate;
	int gain;
	int mappingFamily;
	int streamCount;
	int coupledCount;
	byte[] channelMapping;

	public OpusHeader(OggPage page) throws IOException {
		SKDSByteBuf input = new SKDSByteBuf(ByteBuffer.wrap(page.getData()).order(ByteOrder.LITTLE_ENDIAN));
		input.skipBytes(8); // magic
		this.version = input.readUnsignedByte();
		this.channels = input.readUnsignedByte();
		this.skip = input.readUnsignedShort();
		this.sampleRate = input.readInt();
		this.gain = input.readUnsignedShort();
		this.mappingFamily = input.readUnsignedByte();
		if (mappingFamily != 0) {
			this.streamCount = input.readUnsignedByte();
			this.coupledCount = input.readUnsignedByte();
			this.channelMapping = new byte[channels];
			input.readFully(this.channelMapping);
		}

		if (version != 1) throw new UnsupportedEncodingException("Unsupported version " + version);
	}


	@Override
	public String toString() {
		return SosisonUtils.toJson(this);
	}
}
