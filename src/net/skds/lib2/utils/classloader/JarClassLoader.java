package net.skds.lib2.utils.classloader;

import lombok.CustomLog;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@CustomLog
public final class JarClassLoader extends ClassLoader {

	private static final int DOT_CLASS_LENGTH = ".class".length();
	private final String PROTOCOL = "jcl-" + getClass().getSimpleName() + hashCode();


	private final Map<String, byte[]> resourceMap;
	private final MemoryURLStreamHandler handler;

	public JarClassLoader(Path jarPath) {
		super(JarClassLoader.class.getClassLoader());
		Map<String, byte[]> rm = new HashMap<>();
		try (InputStream is = Files.newInputStream(jarPath, StandardOpenOption.READ)) {
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				if (entry.isDirectory()) continue;
				byte[] bytes = zis.readAllBytes();
				String name = processClassName(entry.getName());
				rm.put(name, bytes);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		this.resourceMap = rm;
		this.handler = new MemoryURLStreamHandler();
	}

	public Collection<String> listResources() {
		return Collections.unmodifiableCollection(resourceMap.keySet());
	}

	private static String processClassName(String fileName) {
		if (fileName.endsWith(".class")) {
			return fileName.substring(0, fileName.length() - DOT_CLASS_LENGTH).replace('/', '.');
		}
		return fileName;
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		synchronized (getClassLoadingLock(name)) {
			Class<?> c = findLoadedClass(name);
			if (c == null) {
				c = findOrCompileClass(name);
				if (c == null) {
					c = getParent().loadClass(name);
				}
				if (resolve) {
					resolveClass(c);
				}
			}
			return c;
		}
	}

	@Override
	public URL getResource(String name) {
		if (resourceMap.containsKey(processClassName(name))) {
			return findResource(name);
		}
		return getParent().getResource(name);
	}

	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		URL u = findResource(name);
		Enumeration<URL> e = getParent().getResources(name);
		if (u == null) {
			return e;
		} else {
			List<URL> list = new ArrayList<>();
			list.add(u);
			while (e.hasMoreElements()) {
				list.add(e.nextElement());
			}
			return Collections.enumeration(list);
		}
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		var foundOrCompiledClass = findOrCompileClass(name);
		if (foundOrCompiledClass == null) {
			throw new ClassNotFoundException(name);
		}
		return foundOrCompiledClass;
	}

	private Class<?> findOrCompileClass(String name) {
		byte[] bytes = resourceMap.get(name);
		if (bytes == null) {
			return null;
		}
		return defineClass(name, bytes, 0, bytes.length);
	}

	@Override
	protected URL findResource(String moduleName, String name) throws IOException {
		if (moduleName == null) {
			return getResource(name);
		}
		return super.findResource(moduleName, name);
	}

	@Override
	protected URL findResource(String name) {
		if (name == null) return null;
		String binaryName = processClassName(name);
		if (resourceMap.get(binaryName) == null) {
			return null;
		}

		try {
			var uri = new URI(PROTOCOL, name, null);
			return URL.of(uri, handler);
		} catch (URISyntaxException | MalformedURLException e) {
			return null;
		}
	}

	@Override
	public Enumeration<URL> findResources(String name) {
		return new Enumeration<>() {
			private URL next = findResource(name);

			@Override
			public boolean hasMoreElements() {
				return (next != null);
			}

			@Override
			public URL nextElement() {
				if (next == null) {
					throw new NoSuchElementException();
				}
				URL u = next;
				next = null;
				return u;
			}
		};
	}

	private class MemoryURLStreamHandler extends URLStreamHandler {
		@Override
		public URLConnection openConnection(URL u) {
			if (!u.getProtocol().equalsIgnoreCase(PROTOCOL)) {
				throw new IllegalArgumentException(u.toString());
			}
			return new MemoryURLConnection(u, resourceMap.get(processClassName(u.getPath())));
		}

	}

	private static class MemoryURLConnection extends URLConnection {
		private final byte[] bytes;
		private InputStream in;

		MemoryURLConnection(URL u, byte[] bytes) {
			super(u);
			this.bytes = bytes;
		}

		@Override
		public void connect() throws IOException {
			if (!connected) {
				if (bytes == null) {
					throw new FileNotFoundException(getURL().getPath());
				}
				in = new ByteArrayInputStream(bytes);
				connected = true;
			}
		}

		@Override
		public InputStream getInputStream() throws IOException {
			connect();
			return in;
		}

		@Override
		public long getContentLengthLong() {
			return bytes.length;
		}

		@Override
		public String getContentType() {
			return "application/octet-stream";
		}
	}
}
