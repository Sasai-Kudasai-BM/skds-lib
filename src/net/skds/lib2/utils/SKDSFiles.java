package net.skds.lib2.utils;

import lombok.experimental.UtilityClass;
import net.skds.lib2.natives.MemoryAccess;

import java.io.*;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@UtilityClass
public class SKDSFiles {

	public static final OpenOption[] DEFAULT_OPTIONS = {StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE};

	public static final Path DESKTOP_PATH;

	public static void deleteDirectory(File dir) {
		if (dir.isDirectory()) {
			for (File file : Objects.requireNonNull(dir.listFiles())) {
				if (file.isFile()) {
					if (!file.delete()) {
						throw new RuntimeException("Unable to file " + file.getAbsolutePath());
					}
				} else {
					deleteDirectory(file);
					if (!file.delete()) {
						throw new RuntimeException("Unable to directory " + file.getAbsoluteFile());
					}
				}
			}
			if (!dir.delete()) {
				throw new RuntimeException("Unable to directory " + dir.getAbsoluteFile());
			}
		}
	}

	public static void collectFileTree(File root, Collection<File> collection) {
		collectFileTree(root, f -> true, collection);
	}

	public static List<File> collectFileTree(File root) {
		ArrayList<File> files = new ArrayList<>();
		collectFileTree(root, f -> true, files);
		return files;
	}

	public static List<File> collectFileTree(File root, Predicate<File> filter) {
		ArrayList<File> files = new ArrayList<>();
		collectFileTree(root, filter, files);
		return files;
	}

	public static List<File> collectFileTree(File root, Predicate<File> filter, int depth) {
		ArrayList<File> files = new ArrayList<>();
		collectFileTree(root, filter, files, depth);
		return files;
	}

	public static void collectFileTree(File root, Predicate<File> filter, Collection<File> collection) {
		collectFileTree(root, filter, collection, Integer.MAX_VALUE);
	}

	public static void collectFileTree(File root, Predicate<File> filter, Collection<File> collection, int depth) {
		if (root.isDirectory()) {
			if (depth <= 0) {
				return;
			}
			File[] files = root.listFiles();
			if (files != null) for (int i = 0; i < files.length; i++) {
				collectFileTree(files[i], filter, collection, depth - 1);
			}
		} else if (filter.test(root)) {
			collection.add(root);
		}
	}

	public static void collectFilesAndDirs(File root, Collection<File> collection) {
		collectFilesAndDirs(root, f -> true, collection);
	}

	public static List<File> collectFiles(File root) {
		ArrayList<File> files = new ArrayList<>();
		collectFilesAndDirs(root, File::isFile, files);
		return files;
	}

	public static List<File> collectDirs(File root) {
		ArrayList<File> files = new ArrayList<>();
		collectFilesAndDirs(root, File::isDirectory, files);
		return files;
	}

	public static List<File> collectFiles(File root, int depth) {
		ArrayList<File> files = new ArrayList<>();
		collectFilesAndDirs(root, File::isFile, files, depth);
		return files;
	}

	public static List<File> collectDirs(File root, int depth) {
		ArrayList<File> files = new ArrayList<>();
		collectFilesAndDirs(root, File::isDirectory, files, depth);
		return files;
	}

	public static List<File> collectFilesAndDirs(File root, Predicate<File> filter) {
		ArrayList<File> files = new ArrayList<>();
		collectFilesAndDirs(root, filter, files);
		return files;
	}

	public static List<File> collectFilesAndDirs(File root, Predicate<File> filter, int depth) {
		ArrayList<File> files = new ArrayList<>();
		collectFilesAndDirs(root, filter, files, depth);
		return files;
	}

	public static void collectFilesAndDirs(File root, Predicate<File> filter, Collection<File> collection) {
		collectFilesAndDirs(root, filter, collection, Integer.MAX_VALUE);
	}

	public static void collectFilesAndDirs(File root, Predicate<File> filter, Collection<File> collection, int depth) {
		if (root.isDirectory()) {
			if (depth <= 0) {
				return;
			}
			File[] files = root.listFiles();
			if (files != null) for (int i = 0; i < files.length; i++) {
				collectFileTree(files[i], filter, collection, depth - 1);
			}
		}
		if (filter.test(root)) {
			collection.add(root);
		}
	}

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

	static {
		Path desktopPath = null;
		try {
			desktopPath = Path.of(System.getProperty("user.home"), "Desktop");
		} catch (Throwable t) {
			t.printStackTrace(System.err);
		}
		DESKTOP_PATH = desktopPath;
	}

}
