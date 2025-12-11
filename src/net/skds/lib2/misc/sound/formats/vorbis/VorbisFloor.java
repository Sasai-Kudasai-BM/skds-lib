package net.skds.lib2.misc.sound.formats.vorbis;

import net.skds.lib2.io.codec.SosisonUtils;

public abstract class VorbisFloor implements VorbisReadable {

	@Override
	public String toString() {
		return SosisonUtils.toJson(this);
	}
}
