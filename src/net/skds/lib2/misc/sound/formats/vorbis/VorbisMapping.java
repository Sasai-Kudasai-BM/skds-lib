package net.skds.lib2.misc.sound.formats.vorbis;

import lombok.AccessLevel;
import lombok.Getter;
import net.skds.lib2.io.codec.CodecRole;
import net.skds.lib2.io.codec.annotation.CodecRoleConstrains;
import net.skds.lib2.io.exception.ParseException;

import java.io.IOException;

@Getter
@CodecRoleConstrains(CodecRole.SERIALIZE)
public class VorbisMapping extends VorbisFloor {

	@Getter(AccessLevel.NONE)
	private final transient VorbisProcessor processor;
	private byte[] mux;
	private int[] submapFloor;
	private int[] submapResidue;
	private int[] magnitude;
	private int[] angle;

	public VorbisMapping(VorbisProcessor processor) {
		this.processor = processor;
	}

	@Override
	public void read(ByteArrayBitInputStream input) throws IOException {
		int submaps = 1;
		if (input.readBit()) {
			submaps = input.readInt(4) + 1;
		}
		int couplingSteps = 0;
		if (input.readBit()) {
			couplingSteps = input.readInt(8) + 1;
		}
		magnitude = new int[couplingSteps];
		angle = new int[couplingSteps];
		int c = VorbisUtils.iLog(processor.getIdentificationHeader().getChannels() - 1);
		for (int i = 0; i < couplingSteps; i++) {
			magnitude[i] = input.readInt(c);
			angle[i] = input.readInt(c);
		}
		if (input.readInt(2) != 0) throw err();
		if (submaps > 1) {
			mux = new byte[submaps];
			for (int i = 0; i < submaps; i++) {
				int v = input.readInt(4);
				if (v >= submaps) throw err();
				mux[i] = (byte) v;
			}
		}
		submapFloor = new int[submaps];
		submapResidue = new int[submaps];
		for (int i = 0; i < submaps; i++) {
			input.readInt(8);
			int floor = input.readInt(8);
			if (floor >= processor.getSetupHeader().getFloorTypes().length) throw err();
			submapFloor[i] = floor;
			int residue = input.readInt(8);
			if (residue >= processor.getSetupHeader().getResidueTypes().length) throw err();
			submapResidue[i] = residue;
		}

	}

	private IOException err() {
		return new ParseException("Vorbis error condition in mappings");
	}

}
