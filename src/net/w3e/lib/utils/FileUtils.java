package net.w3e.lib.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FileUtils {

	public static void save(File file, byte[] data) {
		try {
			if (!file.exists()) {
				file.getAbsoluteFile().getParentFile().mkdirs();
				Files.createFile(file.toPath());
			}
			Files.write(file.toPath(), data);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void copy(File in, File out) {
		try {
			out.getAbsoluteFile().getParentFile().mkdirs();
			Files.copy(in.toPath(), out.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
