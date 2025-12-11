package net.skds.lib2.misc.sound.formats.vorbis;

import lombok.Getter;
import net.skds.lib2.io.codec.CodecRole;
import net.skds.lib2.io.codec.annotation.CodecRoleConstrains;
import net.skds.lib2.io.exception.ParseException;

import java.io.IOException;

@Getter
@CodecRoleConstrains(CodecRole.SERIALIZE)
public class VorbisSetupHeader extends AbstractVorbisHeader {

	private VorbisCodebook[] codebooks;

	private int[] floorTypes;
	private VorbisFloor0[] floors0;
	private VorbisFloor1[] floors1;

	private int[] residueTypes;
	private VorbisResidue[] residues;

	private VorbisMapping[] mappings;

	private VorbisMode[] modes;

	private final transient VorbisProcessor processor;

	public VorbisSetupHeader(VorbisProcessor processor) {
		super(5);
		this.processor = processor;
	}

	@Override
	public void read(ByteArrayBitInputStream input) throws IOException {
		super.read(input);

		int cc = input.readAlignedInt(1) + 1;
		this.codebooks = new VorbisCodebook[cc];
		for (int i = 0; i < cc; i++) {
			VorbisCodebook codebook = new VorbisCodebook();
			this.codebooks[i] = codebook;
			codebook.read(input);
		}

		int tc = input.readInt(6) + 1;
		for (int i = 0; i < tc; i++) {
			if (input.readInt(16) != 0) throw new ParseException("Vorbis error condition in time domain transforms");
		}

		int fc = input.readInt(6) + 1;
		floorTypes = new int[fc];
		this.floors0 = new VorbisFloor0[fc];
		this.floors1 = new VorbisFloor1[fc];
		for (int i = 0; i < fc; i++) {
			int floorType = input.readInt(16);
			if (floorType == 0) {
				VorbisFloor0 floor = new VorbisFloor0();
				floors0[i] = floor;
				floor.read(input);
			} else if (floorType == 1) {
				VorbisFloor1 floor = new VorbisFloor1();
				floors1[i] = floor;
				floor.read(input);
			} else {
				throw new ParseException("Vorbis error condition in floors");
			}
			floorTypes[i] = floorType;
		}

		int rc = input.readInt(6) + 1;
		this.residueTypes = new int[rc];
		this.residues = new VorbisResidue[rc];
		for (int i = 0; i < rc; i++) {
			int t = input.readInt(16);
			if (t > 2) {
				throw new ParseException("Vorbis error condition in residues");
			}
			this.residueTypes[i] = t;
			VorbisResidue residue = new VorbisResidue();
			residue.read(input);
			this.residues[i] = residue;
		}

		int mappingCount = input.readInt(6) + 1;
		this.mappings = new VorbisMapping[mappingCount];
		for (int i = 0; i < mappingCount; i++) {
			if (input.readInt(16) != 0) {
				throw new ParseException("Vorbis error condition in mappings");
			}
			VorbisMapping mapping = new VorbisMapping(processor);
			this.mappings[i] = mapping;
			mapping.read(input);
		}

		int modeCount = input.readInt(6) + 1;
		this.modes = new VorbisMode[modeCount];
		for (int i = 0; i < modeCount; i++) {
			VorbisMode mode = new VorbisMode(processor);
			mode.read(input);
			this.modes[i] = mode;
		}
		if (!input.readBit()) throw new ParseException("Vorbis farming error");

		//if (!validate()) throw new ParseException("Validation failed");
	}

	@Override
	protected boolean validate() {
		return true;
	}
}
