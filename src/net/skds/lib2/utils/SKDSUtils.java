package net.skds.lib2.utils;

import com.sun.management.HotSpotDiagnosticMXBean;
import lombok.AllArgsConstructor;
import lombok.CustomLog;
import lombok.NoArgsConstructor;
import net.skds.lib2.io.ByteArrayExtendedDataOutput;
import net.skds.lib2.mat.ByteArrayPrimitiveOperations;

import javax.management.MBeanServer;
import java.awt.*;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.math.BigInteger;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@SuppressWarnings("unused")
@CustomLog
public class SKDSUtils {

	public static final HexFormat HEX_FORMAT_LC = StringUtils.HEX_FORMAT_LC;
	public static final OSType OS_TYPE = getOS();
	public static final String OS_ARC = getOSAndArc();
	public static final int DEFAULT_BUFFER_SIZE = Integer.getInteger("skds.default_buffer_size", 8192);

	public static final Runnable EMPTY_RUNNABLE = () -> {
	};
	public static final Predicate<?> TRUE_PREDICATE = o -> true;

	public static final Random R = new Random();

	public static final UUID NULL_UUID = new UUID(0, 0);

	private static final Function<Throwable, Object> CATCHER = t -> {
		t.printStackTrace();
		return null;
	};
	private static final Consumer<?> EMPTY_CONSUMER = o -> {
	};

	private static Supplier<MessageDigest> SHA1 = getMDSafe("SHA1", md -> SHA1 = md);
	private static Supplier<MessageDigest> SHA256 = getMDSafe("SHA256", md -> SHA256 = md);
	private static Supplier<MessageDigest> SHA512 = getMDSafe("SHA512", md -> SHA512 = md);
	private static Supplier<MessageDigest> MD5 = getMDSafe("MD5", md -> MD5 = md);

	@SuppressWarnings("unchecked")
	public static <T> Function<Throwable, ? extends T> getCatcher() {
		return (Function<Throwable, ? extends T>) CATCHER;
	}

	@SuppressWarnings("unchecked")
	public static <T> Consumer<T> emptyConsumer() {
		return (Consumer<T>) EMPTY_CONSUMER;
	}

	private static Supplier<MessageDigest> getMDSafe(String algorithm, Consumer<Supplier<MessageDigest>> consumer) {
		return () -> {
			Supplier<MessageDigest> sup;
			try {
				MessageDigest md0 = MessageDigest.getInstance(algorithm);
				try {
					MessageDigest md = (MessageDigest) md0.clone();
					sup = () -> {
						try {
							return (MessageDigest) md.clone();
						} catch (CloneNotSupportedException e) {
							throw new RuntimeException(e);
						}
					};
				} catch (CloneNotSupportedException e) {
					log.warn("MD algorithm \"" + algorithm + "\" is not Cloneable");
					sup = () -> {
						try {
							return MessageDigest.getInstance(algorithm);
						} catch (NoSuchAlgorithmException ex) {
							throw new RuntimeException(ex);
						}
					};
				}
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException(e);
			}
			consumer.accept(sup);
			return sup.get();
		};
	}

	public static int getDefaultBufferSize(long dataLen) {
		return dataLen < DEFAULT_BUFFER_SIZE ? (int) dataLen : DEFAULT_BUFFER_SIZE;
	}

	public static int getDefaultBufferSize(int dataLen) {
		return dataLen < DEFAULT_BUFFER_SIZE ? dataLen : DEFAULT_BUFFER_SIZE;
	}

	public static byte[] createOptimalSizedBuffer(long dataLen) {
		if (dataLen == 0) return ArrayUtils.EMPTY_BYTE;
		return new byte[dataLen < DEFAULT_BUFFER_SIZE ? (int) dataLen : DEFAULT_BUFFER_SIZE];
	}

	public static byte[] createOptimalSizedBuffer(int dataLen) {
		if (dataLen == 0) return ArrayUtils.EMPTY_BYTE;
		return new byte[dataLen < DEFAULT_BUFFER_SIZE ? dataLen : DEFAULT_BUFFER_SIZE];
	}


	public static <T> T castOrThrow(Object o, Class<T> type, String message) {
		try {
			return type.cast(o);
		} catch (ClassCastException e) {
			throw new RuntimeException(message, e);
		}
	}

	public static <T> T castOrThrow(Object o, Class<T> type) {
		try {
			return type.cast(o);
		} catch (ClassCastException e) {
			throw new RuntimeException("Not a " + type.getSimpleName(), e);
		}
	}

	public static String hashFile(File f) {
		try (InputStream is = new FileInputStream(f)) {
			byte[] buffer = SKDSUtils.createOptimalSizedBuffer(f.length());
			MessageDigest md = getSHA1();
			if (!f.exists()) {
				return HEX_FORMAT_LC.formatHex(md.digest());
			}
			int c;
			while ((c = is.read(buffer)) >= 0) {
				md.update(buffer, 0, c);
			}
			return HEX_FORMAT_LC.formatHex(md.digest());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] hashFileArray(File f) {
		try (InputStream is = new BufferedInputStream(new FileInputStream(f))) {
			byte[] buffer = SKDSUtils.createOptimalSizedBuffer(f.length());
			MessageDigest md = getSHA1();
			if (!f.exists()) {
				return md.digest();
			}
			int c;
			while ((c = is.read(buffer)) >= 0) {
				md.update(buffer, 0, c);
			}
			return md.digest();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String hashInput(InputStream is) {
		try {
			byte[] buffer = new byte[1024];
			MessageDigest md = getSHA1();
			int c;
			while ((c = is.read(buffer)) >= 0) {
				md.update(buffer, 0, c);
			}
			return HEX_FORMAT_LC.formatHex(md.digest());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String hashArray(byte[] data) {
		MessageDigest md = getSHA1();
		return HEX_FORMAT_LC.formatHex(md.digest(data));
	}

	public static String hashString(String data) {
		MessageDigest md = getSHA1();
		return HEX_FORMAT_LC.formatHex(md.digest(data.getBytes(StandardCharsets.UTF_8)));
	}

	public static StartTime startTimeMeasure() {
		return new StartTime(System.nanoTime());
	}

	public static void openInBrowser(String link) {
		try {
			Desktop.getDesktop().browse(URI.create(link));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Deprecated(forRemoval = true)
	public static int arrToInt(byte[] arr) {
		return (((arr[0] & 255) << 8 | arr[1] & 255) << 8 | arr[2] & 255) << 8 | arr[3] & 255;
	}

	@Deprecated(forRemoval = true)
	public static long arrToLong(byte[] arr) {
		BigInteger bi = new BigInteger(arr);
		return ((((((((long) (arr[0] & 255) << 8 | arr[1] & 255) << 8 | arr[2] & 255) << 8 | arr[3] & 255) << 8 | arr[4] & 255) << 8) | arr[5] & 255) << 8 | arr[6] & 255) << 8 | arr[7] & 255;
	}

	public static int[] UUID2IntArray(UUID uuid) {
		long l = uuid.getLeastSignificantBits();
		long m = uuid.getMostSignificantBits();
		return new int[]{(int) (m >> 32), (int) m, (int) (l >> 32), (int) l};
	}

	public static UUID IntArray2UUID(int[] uuid) {
		assert (uuid.length == 4) : "uuid int array length must be 4";
		long m = (long) uuid[0] << 32;
		m |= uuid[1];
		long l = (long) uuid[2] << 32;
		l |= uuid[3];
		return new UUID(m, l);
	}

	public static UUID uuid(byte[] uuid) {
		return new UUID(ByteArrayPrimitiveOperations.getLong(uuid, 0), ByteArrayPrimitiveOperations.getLong(uuid, 8));
	}

	public static String memoryCompact(long bytes) {
		if (bytes < 1L << 10) {
			return bytes + " Bytes";
		} else if (bytes < 1L << 20) {
			return "%.2f kBytes".formatted(bytes / 1024d);
		} else if (bytes < 1L << 30) {
			return "%.2f MBytes".formatted(bytes / (1024d * 1024));
		} else if (bytes < 1L << 40) {
			return "%.2f GBytes".formatted(bytes / (1024d * 1024 * 1024));
		} else {
			return "%.2f TBytes".formatted(bytes / (1024d * 1024 * 1024 * 1024));
		}
	}

	public static String memoryCompact(long bytes, long of) {
		if (of < 1L << 10) {
			return "%s / %s Bytes".formatted(bytes, of);
		} else if (of < 1L << 20) {
			return "%.2f / %.2f kBytes".formatted(bytes / 1024d, of / 1024d);
		} else if (of < 1L << 30) {
			return "%.2f / %.2f MBytes".formatted(bytes / (1024d * 1024), of / (1024d * 1024));
		} else if (of < 1L << 40) {
			return "%.2f / %.2f GBytes".formatted(bytes / (1024d * 1024 * 1024), of / (1024d * 1024 * 1024));
		} else {
			return "%.2f / %.2f TBytes".formatted(bytes / (1024d * 1024 * 1024 * 1024), of / (1024d * 1024 * 1024 * 1024));
		}
	}

	public static String stringHash(MessageDigest md, String value) {
		return HEX_FORMAT_LC.formatHex(md.digest(value.getBytes(StandardCharsets.UTF_8)));
	}

	public static <T> void consumeIfNotNull(T object, Consumer<T> action) {
		if (object != null) {
			action.accept(object);
		}
	}

	public static UUID uuidFromName(String name) {
		return UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
	}


	@Deprecated(forRemoval = true)
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PairIF {
		public int i;
		public float f;

		@Override
		public String toString() {
			return String.format("[%s <-> %s]", i, f);
		}
	}

	@Deprecated(forRemoval = true)
	public static class Pair<A, B> {
		public A a;
		public B b;

		public Pair() {
		}

		public Pair(A a, B b) {
			this.a = a;
			this.b = b;
		}

		@Override
		public String toString() {
			return String.format("[%s <-> %s]", a, b);
		}
	}

	@Deprecated(forRemoval = true)
	public static class Numered<T> {
		public T value;
		public int index;

		public Numered(T value, int index) {
			this.value = value;
			this.index = index;
		}

		@Override
		public String toString() {
			return String.format("[%s <-> %s]", value, index);
		}
	}

	@Deprecated(forRemoval = true)
	public static class NumeredConstant<T> {
		public final T value;
		public final int index;

		public NumeredConstant(T value, int index) {
			this.value = value;
			this.index = index;
		}

		@Override
		public String toString() {
			return String.format("[%s <-> %s]", value, index);
		}
	}

	public interface Indexed {
		int getIndex();
	}

	public interface NamedWithId {
		String getName();

		String getId();
	}

	@Deprecated
	public static class R3ICorded<T> {
		public T object;
		public int x;
		public int y;
		public int z;

		public R3ICorded(T o, int x, int y, int z) {
			this.object = o;
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public R3ICorded() {
			this.object = null;
			this.x = 0;
			this.y = 0;
			this.z = 0;
		}

		@Override
		public String toString() {
			return String.format("[(%s, %s, %s) <-> %s]", x, y, z, object);
		}
	}


	@Deprecated(forRemoval = true)
	public static class R3ICordedC<T> {
		public final T object;
		public final int x;
		public final int y;
		public final int z;

		public R3ICordedC(T o, int x, int y, int z) {
			this.object = o;
			this.x = x;
			this.y = y;
			this.z = z;
		}

		@Override
		public String toString() {
			return String.format("[(%s, %s, %s) <-> %s]", x, y, z, object);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> Predicate<T> truePredicate() {
		return (Predicate<T>) TRUE_PREDICATE;
	}

	public static void collectFileTree(File root, Collection<File> collection) {
		SKDSFiles.collectFileTree(root, f -> true, collection);
	}

	public static List<File> collectFileTree(File root) {
		ArrayList<File> files = new ArrayList<>();
		SKDSFiles.collectFileTree(root, f -> true, files);
		return files;
	}

	public static List<File> collectFileTree(File root, Predicate<File> filter) {
		ArrayList<File> files = new ArrayList<>();
		SKDSFiles.collectFileTree(root, filter, files);
		return files;
	}

	public static List<File> collectFileTree(File root, Predicate<File> filter, int depth) {
		ArrayList<File> files = new ArrayList<>();
		SKDSFiles.collectFileTree(root, filter, files, depth);
		return files;
	}

	public static void collectFileTree(File root, Predicate<File> filter, Collection<File> collection) {
		SKDSFiles.collectFileTree(root, filter, collection, Integer.MAX_VALUE);
	}

	public static void collectFileTree(File root, Predicate<File> filter, Collection<File> collection, int depth) {
		SKDSFiles.collectFileTree(root, filter, collection, depth);
	}

	public static MessageDigest getSHA1() {
		return SHA1.get();
	}

	public static MessageDigest getSHA256() {
		return SHA256.get();
	}

	public static MessageDigest getSHA512() {
		return SHA512.get();
	}

	public static MessageDigest getMD5() {
		return MD5.get();
	}

	public static ByteBuffer compress(byte[] data, int offset, int len, int compressionLevel) {
		Deflater deflater = new Deflater(compressionLevel);
		deflater.setInput(data, offset, len);
		deflater.finish();
		byte[] outBuffer = new byte[(Math.min(len + 32, SKDSUtils.DEFAULT_BUFFER_SIZE))];
		ByteArrayExtendedDataOutput bao = new ByteArrayExtendedDataOutput(len + 32);
		do {
			int i = deflater.deflate(outBuffer);
			bao.write(outBuffer, 0, i);
		} while (!deflater.finished());

		deflater.end();
		return ByteBuffer.wrap(bao.rawArray(), 0, bao.size());
	}

	public static void compress(byte[] data, int offset, int len, int compressionLevel, DataOutput output) {
		Deflater deflater = new Deflater(compressionLevel);
		deflater.setInput(data, offset, len);
		deflater.finish();
		byte[] outBuffer = new byte[(Math.min(len + 32, SKDSUtils.DEFAULT_BUFFER_SIZE))];
		try {
			do {
				int i = deflater.deflate(outBuffer);
				output.write(outBuffer, 0, i);
			} while (!deflater.finished());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		deflater.end();
	}

	public static ByteBuffer compress(byte[] data, int offset, int len) {
		return compress(data, offset, len, Deflater.DEFAULT_COMPRESSION);
	}

	public static void compress(byte[] data, int offset, int len, DataOutput output) {
		compress(data, offset, len, Deflater.DEFAULT_COMPRESSION, output);
	}

	public static ByteBuffer decompress(byte[] data, int uncompressedSize) throws DataFormatException {
		return decompress(data, 0, data.length, uncompressedSize);
	}

	public static ByteBuffer decompress(byte[] data, int offset, int len, int uncompressedSize) throws DataFormatException {
		Inflater inflater = new Inflater();
		inflater.setInput(data, offset, len);
		ByteBuffer outBuffer = ByteBuffer.allocate(uncompressedSize);
		while (true) {
			inflater.inflate(outBuffer);
			if (!inflater.finished()) {
				if (!outBuffer.hasRemaining()) {
					throw new RuntimeException("Decompressed data size is larger than specified " + uncompressedSize);
				}
			} else break;
		}
		inflater.end();
		return outBuffer.flip();
	}

	private static String getOSAndArc() {
		String arc = System.getProperty("os.arch");
		//System.out.println(OS_TYPE.nameLC + "-" + arc);
		return OS_TYPE.nameLC + "-" + arc;
	}

	private static OSType getOS() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")) {
			return OSType.WINDOWS;
		} else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
			return OSType.LINUX;
		} else if (os.contains("mac")) {
			return OSType.OSX;
		} else if (os.contains("sunos")) {
			return OSType.SOLARIS;
		}
		return OSType.UNKNOWN;
	}

	public static void dumpHeap(String filePath) {
		File f = new File(filePath);
		if (f.exists()) {
			if (!f.delete()) {
				System.out.println("Unable to dump heap: File \"" + filePath + "\" cannot be overwritten");
				return;
			}
		}
		System.out.println("Dumping heap");
		try {
			MBeanServer server = ManagementFactory.getPlatformMBeanServer();
			HotSpotDiagnosticMXBean mxBean = ManagementFactory.newPlatformMXBeanProxy(
					server,
					"com.sun.management:type=HotSpotDiagnostic",
					HotSpotDiagnosticMXBean.class
			);
			mxBean.dumpHeap(filePath, true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		System.out.println("Heap dump created \"" + filePath + "\"");
	}


	public enum OSType {
		WINDOWS, LINUX, OSX, SOLARIS, UNKNOWN;

		public final String nameLC;

		OSType() {
			this.nameLC = name().toLowerCase();
		}
	}
}
