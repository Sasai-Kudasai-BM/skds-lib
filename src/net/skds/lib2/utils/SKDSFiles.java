package net.skds.lib2.utils;

import lombok.experimental.UtilityClass;

import java.io.File;
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
