package net.w3e.lib.utils;

import net.sdteam.libmerge.Lib1Merge;
import net.skds.lib2.io.json.JsonTest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

@Lib1Merge
public class ResourceUtil {

	private static final Path rootPath;

	static {
		ClassLoader classLoader = getContextClassLoader();

		URL resource = classLoader.getResource("");

		Path path = null;
		try {
			URI uri = Objects.requireNonNull(resource).toURI();
			if (!uri.getScheme().equals("jar")) {
				path = Paths.get(resource.toURI());
			}
		} catch (Exception ignored) {
		}
		rootPath = path;
		System.out.println(rootPath);
	}

	private static void walk(Path path, Collection<Path> collection) {
		try (Stream<Path> walk = Files.walk(path, Integer.MAX_VALUE)) {
			for (Iterator<Path> it = walk.iterator(); it.hasNext(); ) {
				Path p = it.next();
				if (!Files.isDirectory(p)) {
					if (rootPath != null) {
						p = rootPath.relativize(p);
					}
					collection.add(p);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static List<Path> getResourceFiles(String root) {
		List<Path> list = new ArrayList<>();
		getResourceFiles(list, root);
		return list;
	}

	public static void getResourceFiles(List<Path> collection, String root) {
		try {
			URI uri = Objects.requireNonNull(JsonTest.class.getClassLoader().getResource(root)).toURI();

			Path myPath;
			if (uri.getScheme().equals("jar")) {
				try {
					FileSystem fs = FileSystems.getFileSystem(uri);
					myPath = fs.getPath(root);
					walk(myPath, collection);
				} catch (FileSystemNotFoundException e) {
					try (FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
						myPath = fileSystem.getPath(root);
						walk(myPath, collection);
					}
				}
			} else {
				myPath = Paths.get(uri);
				walk(myPath, collection);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static InputStream getResourceAsStream(Path resource) {
		return getResourceAsStream(resource.toString());
	}

	public static InputStream getResourceAsStream(String resource) {
		final InputStream in = getContextClassLoader().getResourceAsStream(resource);

		return in == null ? ResourceUtil.class.getResourceAsStream(resource) : in;
	}

	private static ClassLoader getContextClassLoader() {
		return ClassLoader.getSystemClassLoader();
	}

	public static void printClassPath() {
		String classpath = System.getProperty("java.class.path");
		String[] classPathValues = classpath.split(File.pathSeparator);
		System.out.println("ClassPath: " + Arrays.toString(classPathValues));
	}

}
