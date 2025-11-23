package net.skds.lib2.natives;

import lombok.experimental.UtilityClass;
import net.skds.lib2.utils.SKDSUtils;

import java.io.*;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.skds.lib2.utils.SKDSFiles.DEFAULT_OPTIONS;

@UtilityClass
public class NativeFileUtils {

	public static MemorySegment readToNativeMemory(File file) throws IOException {
		return readToNativeMemory(Arena.ofAuto(), file);
	}

	public static MemorySegment readToNativeMemory(Arena arena, File file) throws IOException {
		if (!file.exists()) return MemorySegment.NULL;
		try (InputStream is = new FileInputStream(file)) {
			long readSize = file.length();
			MemorySegment segment = arena.allocate(readSize);
			int bufSize = SKDSUtils.getDefaultBufferSize(readSize);
			byte[] buffer = new byte[bufSize];
			for (int r = 0; r < readSize; ) {
				int read = is.read(buffer);
				if (read == 0) return segment.reinterpret(r);
				MemorySegment.copy(buffer, 0, segment, ValueLayout.JAVA_BYTE, r, read);
				r += read;
			}
			return segment;
		}
	}

	public static void writeFromNativeMemory(Path path, long address, long bytes) throws IOException {
		writeFromNativeMemory(path, MemoryAccess.ALL_MEMORY, address, bytes);
	}

	public static void writeFromNativeMemory(Path path, MemorySegment segment, long offset, long bytes) throws IOException {
		Files.createDirectories(path.getParent());
		if (segment == null) segment = MemoryAccess.ALL_MEMORY;
		try (OutputStream os = Files.newOutputStream(path, DEFAULT_OPTIONS)) {
			int bufSize = SKDSUtils.getDefaultBufferSize(bytes);
			byte[] buffer = new byte[bufSize];
			for (long remaning = bytes; remaning > 0; remaning -= bufSize) {
				MemorySegment.copy(buffer, 0, segment, ValueLayout.JAVA_BYTE, segment.address() + offset + bytes - remaning, (int) Math.min(remaning, bufSize));
				os.write(buffer);
			}
		}
	}
}
