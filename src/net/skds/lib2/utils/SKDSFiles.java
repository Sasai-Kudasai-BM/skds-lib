package net.skds.lib2.utils;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

@UtilityClass
public class SKDSFiles {
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

}
