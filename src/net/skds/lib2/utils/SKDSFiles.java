package net.skds.lib2.utils;

import lombok.CustomLog;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

@CustomLog
@UtilityClass
public class SKDSFiles {

	public static final FileVisitor<Path> DELETER = new FileVisitor<>() {
		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			Files.delete(file);
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
			if (exc instanceof NoSuchFileException) {
				return FileVisitResult.CONTINUE;
			} else {
				throw exc;
			}
		}

		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
			Files.delete(dir);
			return FileVisitResult.CONTINUE;
		}
	};

	public static final OpenOption[] DEFAULT_OPTIONS = {StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE};
	public static final CopyOption[] DEFAULT_COPY_OPTIONS = {StandardCopyOption.REPLACE_EXISTING};
	public static final Set<OpenOption> DEFAULT_OPTIONS_SET = Set.of(DEFAULT_OPTIONS);
	public static final boolean IS_PATH_CANONICAL = File.separatorChar == '/';

	public static final Path DESKTOP_PATH;

	public static String toCanonicalPath(String path) {
		return path.replace('\\', '/');
	}

	public static String toCanonicalPathIfNeeded(String path) {
		return path.replace(File.separatorChar, '/');
	}

	public static <T extends DirtyAble> void saveAsync(Path path, T value, BiConsumer<Path, T> saver) {
		if (value.isDirty()) {
			ThreadUtils.runTaskNewThread(() -> {
				synchronized (path) {
					if (value.isDirty()) {
						saver.accept(path, value);
						value.unmarkDirty();
					}
				}
			});
		}
	}

	public static void copyDirectory(Path src, Path dst) throws IOException {
		if (Files.isDirectory(src)) {
			Files.walkFileTree(src, new FileVisitor<>() {
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					Path destPath = dst.resolve(src.relativize(dir));
					Files.createDirectories(destPath);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					Path destPath = dst.resolve(src.relativize(file));
					Files.copy(file, destPath, DEFAULT_COPY_OPTIONS);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file, IOException exc) {
					log.warn("Failed to copy file \"" + file + "\": " + exc.getMessage());
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
					return FileVisitResult.CONTINUE;
				}
			});
		} else {
			throw new IllegalStateException("Source path is not a directory");
		}
	}

	public static void deleteFileOrDirectory(Path path) throws IOException {
		if (Files.isDirectory(path)) {
			Files.walkFileTree(path, DELETER);
		} else if (Files.isRegularFile(path)) {
			Files.delete(path);
		}
	}

	public static void createFileAndParentDir(Path path) throws IOException {
		Files.createDirectories(path.toAbsolutePath().getParent());
		Files.newByteChannel(path, DEFAULT_OPTIONS_SET).close();
	}

	public static void createFileAndParentDir(Path path, byte[] bytes) throws IOException {
		Files.createDirectories(path.toAbsolutePath().getParent());
		Files.write(path, bytes, DEFAULT_OPTIONS);
	}

	public static void createFileAndParentDir(Path path, String string) throws IOException {
		Files.createDirectories(path.toAbsolutePath().getParent());
		Files.writeString(path, string, DEFAULT_OPTIONS);
	}

	public static void createParentDir(Path path) {
		try {
			Files.createDirectories(path.toAbsolutePath().getParent());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void deleteDirectory(File dir) {
		if (dir.isDirectory()) {
			for (File file : Objects.requireNonNull(dir.listFiles())) {
				if (file.isFile()) {
					if (!file.delete()) {
						throw new RuntimeException("Unable to delete file " + file.getAbsolutePath());
					}
				} else {
					deleteDirectory(file);
					if (!file.delete()) {
						throw new RuntimeException("Unable to delete directory " + file.getAbsoluteFile());
					}
				}
			}
			if (!dir.delete()) {
				throw new RuntimeException("Unable to delete directory " + dir.getAbsoluteFile());
			}
		}
	}

	public static void collectFileTree(File root, Collection<File> collection) {
		collectFileTree(root, SKDSUtils.truePredicate(), collection);
	}

	public static List<File> collectFileTree(File root) {
		ArrayList<File> files = new ArrayList<>();
		collectFileTree(root, SKDSUtils.truePredicate(), files);
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
			if (files != null) for (File file : files) {
				collectFileTree(file, filter, collection, depth - 1);
			}
		} else if (filter.test(root)) {
			collection.add(root);
		}
	}

	public static void collectFilesAndDirs(File root, Collection<File> collection) {
		collectFilesAndDirs(root, SKDSUtils.truePredicate(), collection);
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
