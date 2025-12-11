package net.skds.lib2.misc.sound.formats.vorbis;

import lombok.Getter;
import net.skds.lib2.misc.ogg.OggPage;
import net.skds.lib2.utils.StringUtils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

@Getter
public class VorbisProcessor implements Consumer<OggPage> {

	private VorbisIdentificationHeader identificationHeader;
	private VorbisCommentHeader commentHeader;
	private VorbisSetupHeader setupHeader;
	private VorbisStreamState state = VorbisStreamState.NONE;

	private List<VorbisAudioPacket> audioPackets = new LinkedList<>();

	@Override
	public void accept(OggPage p) {
		ByteArrayBitInputStream in = new ByteArrayBitInputStream(p.getData());
		try {
			if (state == VorbisStreamState.SETUP) {
				state = VorbisStreamState.BODY;
			}
			System.out.println(state + " " + p.getData().length + ": " + StringUtils.HEX_FORMAT_UC.formatHex(p.getData()));
			if (state == VorbisStreamState.BODY) {
				VorbisAudioPacket audioPacket = new VorbisAudioPacket(this);
				audioPacket.read(in);
				this.audioPackets.add(audioPacket);
				return;
			}
			if (state == VorbisStreamState.NONE) { // header
				this.identificationHeader = new VorbisIdentificationHeader();
				this.identificationHeader.read(in);
				//System.out.println(this.identificationHeader);
				state = VorbisStreamState.IDENTIFICATION;
				return;
			}
			if (state == VorbisStreamState.IDENTIFICATION) { // commentheader
				this.commentHeader = new VorbisCommentHeader();
				this.commentHeader.read(in);
				//System.out.println(this.commentHeader);
				state = VorbisStreamState.COMMENT;
				if (in.availableBytes() == 0) return;
			}
			if (state == VorbisStreamState.COMMENT) { // setup
				this.setupHeader = new VorbisSetupHeader(this);
				this.setupHeader.read(in);
				//System.out.println(this.setupHeader);
				state = VorbisStreamState.SETUP;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
