package net.skds.lib2.misc.ogg;

import lombok.Getter;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public final class OggInputStream implements Closeable {
	private final InputStream in;
	@Getter
	private boolean closed = false;
	private OggPage lastPage;

	public OggInputStream(InputStream in) {
		this.in = in;
	}

	public OggPage readPage() throws IOException {
		if (closed) return null;
		OggPage lp = this.lastPage;
		if (lp != null) {
			if (lp.read(in).isDone()) {
				this.lastPage = null;
				return lp.validate() ? lp : null;
			} else {
				return null;
			}
		} else {
			if (in.available() < OggPage.MINIMUM_PAGE_SIZE) {
				return null;
			}
			lp = new OggPage();
			if (lp.read(in).isDone()) {
				return lp.validate() ? lp : null;
			} else {
				this.lastPage = lp;
				return null;
			}
		}
	}

	public List<OggPage> readAllPages() throws IOException {
		OggPage p;
		List<OggPage> pages = new ArrayList<>();
		while ((p = readPage()) != null) {
			pages.add(p);
		}
		return pages;
	}

	@Override
	public void close() throws IOException {
		this.closed = true;
		in.close();
	}
}
