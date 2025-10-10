package net.skds.lib2.storage.filecache;

public record FileChunk(long fileSize, long chunkOffset, byte[] data) {
}
