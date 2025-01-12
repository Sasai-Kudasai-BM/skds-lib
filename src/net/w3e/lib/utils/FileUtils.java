package net.w3e.lib.utils;

import net.sdteam.libmerge.Lib1Merge;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Lib1Merge
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
			e.printStackTrace();
		}
	}

	public static File getParentFile(File file) {
		return file.getAbsoluteFile().getParentFile();
	}

	public static File getParentFile(Path path) {
		return path.toFile().getAbsoluteFile().getParentFile();
	}

	public static File createParentDirs(File file) {
		file = getParentFile(file);
		file.mkdirs();
		return file;
	}
	
	public static boolean createParentDirs(Path path) {
		File file = getParentFile(path);
		return file.mkdirs();
	}
}
