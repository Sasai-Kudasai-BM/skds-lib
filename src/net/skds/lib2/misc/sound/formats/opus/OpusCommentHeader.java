package net.skds.lib2.misc.sound.formats.opus;

import lombok.NoArgsConstructor;
import net.skds.lib2.io.json.JsonUtils;
import net.skds.lib2.misc.ogg.OggPage;
import net.skds.lib2.utils.SKDSByteBuf;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
public class OpusCommentHeader {

	private static final byte[] MAGIC = "OpusTags".getBytes(StandardCharsets.UTF_8);

	String vendor;
	List<String> userComments;

	public OpusCommentHeader(OggPage page) throws IOException {
		if (!Arrays.equals(page.getData(), 0, 8, MAGIC, 0, 8)) {
			throw new IOException("Wrong magic");
		}

		SKDSByteBuf input = new SKDSByteBuf(ByteBuffer.wrap(page.getData()).order(ByteOrder.LITTLE_ENDIAN));
		input.skipBytes(8); // magic

		int vendorLen = input.readInt();
		this.vendor = input.readStringBytes(vendorLen);
		int commentCount = input.readInt();
		List<String> comments = new ArrayList<>(commentCount);
		for (int i = 0; i < commentCount; i++) {
			String comment = input.readStringBytes(input.readInt());
			comments.add(comment);
		}
		this.userComments = comments;
	}


	@Override
	public String toString() {
		return JsonUtils.toJson(this);
	}
}
