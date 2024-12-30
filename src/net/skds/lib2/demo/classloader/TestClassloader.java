package net.skds.lib2.demo.classloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class TestClassloader extends ClassLoader {

	public TestClassloader() {
	}

	@Override
	public Class<?> findClass(String name) throws ClassNotFoundException {
		byte[] b = loadClassFromFile(name);
		return defineClass(name, b, 0, b.length);
	}

	private byte[] loadClassFromFile(String fileName) {
		byte[] buffer;
		try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName.replace('.', '/') + ".class")) {
			if (inputStream == null) throw new NullPointerException();
			return inputStream.readAllBytes();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	@Override
	protected URL findResource(String name) {
		return super.findResource(name);
	}

	@Override
	protected URL findResource(String moduleName, String name) throws IOException {
		return super.findResource(moduleName, name);
	}
}
