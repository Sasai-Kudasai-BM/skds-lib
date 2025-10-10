package net.skds.lib2.storage.filecache;

import net.skds.lib2.utils.SKDSUtils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantLock;

//@CustomLog
public class CachedFileCollection {

	private final int chunkSize;
	private final WeakHashMap<FileChunkKey, FileChunk> chunkCache = new WeakHashMap<>();
	private final ReentrantLock lock = new ReentrantLock();

	public CachedFileCollection() {
		this(SKDSUtils.DEFAULT_BUFFER_SIZE);
	}

	public CachedFileCollection(int chunkSize) {
		this.chunkSize = chunkSize;
	}

	public List<FileChunk> getChunks(String path, long offset, long length) {
		if (length == 0) return List.of();
		int index = Math.toIntExact(offset / chunkSize);
		int count = Math.toIntExact(((length - 1) / chunkSize) + 1);

		List<FileChunk> chunks = new ArrayList<>();

		for (int i = 0; i < count; i++) {
			//log.debug("getting %s %.2fk:%.2fk".formatted(path, offset / 1024f, length / 1024f));
			FileChunkKey key = new FileChunkKey(path, index + i);
			lock.lock();
			FileChunk chunk = chunkCache.computeIfAbsent(key, this::loadChunk);
			lock.unlock();
			if (chunk != null) {
				chunks.add(chunk);
			} else break;
		}
		return chunks;
	}

	public FileChunk getChunk(String path, long offset) {
		//log.debug("getting %s %.2fk".formatted(path, offset / 1024f));
		int index = Math.toIntExact(offset / chunkSize);
		FileChunkKey key = new FileChunkKey(path, index);
		lock.lock();
		FileChunk chunk = chunkCache.computeIfAbsent(key, this::loadChunk);
		lock.unlock();
		return chunk;
	}

	private FileChunk loadChunk(FileChunkKey key) {
		try (RandomAccessFile raf = new RandomAccessFile(key.path(), "r")) {
			long offset = key.chunkIndex() * chunkSize;
			long len = raf.length();
			int size = chunkSize;
			if (len <= offset + size) {
				size = Math.toIntExact(len - offset);
			}
			if (size <= 0) return null;
			raf.seek(offset);
			byte[] data = new byte[size];
			raf.readFully(data);
			//log.debug("read %s %.2fk:%.2fk".formatted(key.path(), offset / 1024f, size / 1024f));
			return new FileChunk(len, offset, data);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
