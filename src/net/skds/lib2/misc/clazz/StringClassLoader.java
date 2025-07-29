package net.skds.lib2.misc.clazz;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.CharBuffer;
import java.util.Collections;

public class StringClassLoader {

	/*private static final List<String> options = new ArrayList<String>();

	static {
		//options.add("-Xlint:-options");
		//options.add("-deprecation");
	}*/

	private final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	private final StandardJavaFileManager sfm = compiler.getStandardFileManager(null, null, null);

	private final InnerCL classLoader = new InnerCL();

	public StringClassLoader() {
	}

	public byte[] compile(String className, String source) throws IOException {
		InnerFM fm = new InnerFM(sfm);
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
		JavaFileObject fileObject = new InnerJavaFileObject(className, source);
		JavaCompiler.CompilationTask ct = compiler.getTask(null, fm, diagnostics, null, null, Collections.singleton(fileObject));
		Boolean call = ct.call();
		if (call == null || !call) {
			for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
				System.err.println(diagnostic);
			}
			throw new IOException("Class compilation error");
		}
		return fm.getBytecode();
	}

	public Class<?> load(String className, String source) throws IOException {
		return load(className, compile(className, source));
	}

	public Class<?> load(String className, byte[] bytecode) throws IOException {
		return classLoader.compile(bytecode, className);
	}

	private static URI createURI(String fileName) {
		String uri = "scl:///" + fileName.replace('.', '/') + ".java";
		return URI.create(uri);
	}

	private static class InnerJavaFileObject extends SimpleJavaFileObject {
		final String sourcecode;
		final ByteArrayOutputStream bytecode;

		InnerJavaFileObject(String className, String code) {
			super(createURI(className), Kind.SOURCE);
			this.sourcecode = code;
			this.bytecode = null;
		}

		InnerJavaFileObject(String className, ByteArrayOutputStream bytecodeOutput) {
			super(createURI(className), Kind.SOURCE);
			this.sourcecode = null;
			this.bytecode = bytecodeOutput;
		}

		@Override
		public CharBuffer getCharContent(boolean ignoreEncodingErrors) {
			return CharBuffer.wrap(sourcecode);
		}

		@Override
		public OutputStream openOutputStream() {
			return this.bytecode;
		}
	}

	private static final class InnerFM extends ForwardingJavaFileManager<JavaFileManager> {

		private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		InnerFM(JavaFileManager fileManager) {
			super(fileManager);
		}

		byte[] getBytecode() {
			return outputStream.toByteArray();
		}

		@Override
		public JavaFileObject getJavaFileForOutput(
				JavaFileManager.Location location,
				String className,
				JavaFileObject.Kind kind,
				FileObject sibling
		) {
			if (kind == JavaFileObject.Kind.CLASS) {
				return new InnerJavaFileObject(className, outputStream);
			} else {
				throw new UnsupportedOperationException("Only class loading supported");
			}
		}
	}

	private static final class InnerCL extends ClassLoader {

		InnerCL() {
			super("StringClassLoader", StringClassLoader.class.getClassLoader());
		}

		Class<?> compile(byte[] bytes, String name) {
			return defineClass(name, bytes, 0, bytes.length);
		}
	}
}