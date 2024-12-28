package net.skds.lib2.utils.logger;

public final class SKDSLoggerFactory {
	private SKDSLoggerFactory() {
	}

	public static SKDSLogger getLogger() {
		Class<?> c;
		try {
			c = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return new SKDSLoggerImpl(c);
	}

	public static SKDSLogger getLogger(Class<?> c) {
		return new SKDSLoggerImpl(c);
	}
}
