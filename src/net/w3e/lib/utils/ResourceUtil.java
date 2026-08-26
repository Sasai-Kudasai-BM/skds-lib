package net.w3e.lib.utils;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

@UtilityClass
public class ResourceUtil {

	public static List<String> listAllResources(String folderPath) {
		List<String> resultFiles = new ArrayList<>();
		ClassLoader classLoader = ResourceUtil.class.getClassLoader();

		try {
			Enumeration<URL> urls = classLoader.getResources(folderPath);

			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				URI uri = url.toURI();

				if ("jar".equals(uri.getScheme())) {
					FileSystem fileSystem;
					boolean closeFs = false;
					try {
						fileSystem = FileSystems.getFileSystem(uri);
					} catch (FileSystemNotFoundException e) {
						fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
						closeFs = true;
					}

					try {
						Path pathInJar = fileSystem.getPath(folderPath);
						scanPath(pathInJar, folderPath, resultFiles);
					} finally {
						if (closeFs) {
							fileSystem.close();
						}
					}
				} else if ("file".equals(uri.getScheme())) {
					Path pathOnDisk = Paths.get(uri);
					scanPath(pathOnDisk, folderPath, resultFiles);
				}
			}
		} catch (Exception e) {
			System.err.println("Error while scan resources: " + e.getMessage());
		}

		return resultFiles;
	}

	private static void scanPath(Path rootPath, String originalFolder, List<String> resultList) throws IOException {
		if (!Files.exists(rootPath)) return;

		try (Stream<Path> walk = Files.walk(rootPath)) {
			walk.filter(Files::isRegularFile).forEach(path -> {
				String rawPath = path.toString();

				rawPath = rawPath.replace("\\", "/");

				int startIndex = rawPath.indexOf(originalFolder);
				if (startIndex != -1) {
					String cleanResourcePath = rawPath.substring(startIndex);
					resultList.add(cleanResourcePath);
				}
			});
		}
	}

	public static InputStream getResourceAsStream(Path resource) {
		return getResourceAsStream(resource.toString());
	}

	public static InputStream getResourceAsStream(String resource) {
		return ResourceUtil.class.getClassLoader().getResourceAsStream(resource);
	}

	public static void printClassPath() {
		String classpath = System.getProperty("java.class.path");
		String[] classPathValues = classpath.split(File.pathSeparator);
		System.out.println("ClassPath: " + Arrays.toString(classPathValues));
	}

}
