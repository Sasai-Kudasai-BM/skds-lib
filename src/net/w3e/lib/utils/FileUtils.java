package net.w3e.lib.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.stream.Stream;

public class FileUtils {

	public static final File ABSOLUTE_ROOT = new File("").getAbsoluteFile();

	public static void save(File file, byte[] data) {
		try {
			if (!file.exists()) {
				createParentDirs(file);
				Files.createFile(file.toPath());
			}
			Files.write(file.toPath(), data);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void copy(File in, File out) {
		try {
			createParentDirs(out);
			Files.copy(in.toPath(), out.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}

	public static void copyFiles(File source, File dest) throws IOException {
		Path sourceFilePath = source.toPath();
		try (Stream<Path> stream = Files.walk(sourceFilePath)) {
			Path destFilePath = dest.toPath();
			stream.forEach(sourcePath -> {
				try {
					// Resolve the corresponding path in the destination
					Path targetPath = destFilePath.resolve(sourceFilePath.relativize(sourcePath));

					if (Files.isDirectory(sourcePath)) {
						if (!Files.exists(targetPath)) {
							Files.createDirectories(targetPath);
						}
					} else {
						Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
					}
				} catch (IOException e) {
					throw new RuntimeException("Failed to copy: " + sourcePath, e);
				}
			});
		}
	}

	public static File getParentFile(File file) {
		return file.getAbsoluteFile().getParentFile();
	}

	public static File getParentFile(Path path) {
		return path.toFile().getAbsoluteFile().getParentFile();
	}

	public static void createParentDirs(File file) {
		file = getParentFile(file);
		file.mkdirs();
	}

	public static void createFileAndParentDirs(File file) {
		createParentDirs(file);
		try {
			if (!file.exists()) {
				if (!file.createNewFile()) {
					throw new IOException("Unable to create file " + file);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void createParentDirs(Path path) {
		File file = getParentFile(path);
		file.mkdirs();
	}

	public static void deleteFilesFromDirectory(File folder) {
		if (folder.isDirectory()) {
			for (File file : Objects.requireNonNull(folder.listFiles())) {
				if (file.isFile()) {
					file.delete();
				} else {
					deleteFilesFromDirectory(file);
				}
			}
		}
	}
}
