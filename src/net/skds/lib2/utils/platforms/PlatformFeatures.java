package net.skds.lib2.utils.platforms;

import net.skds.lib2.utils.SKDSUtils;

import java.awt.event.KeyListener;

public abstract sealed class PlatformFeatures permits WindowsPlatform {

	private static PlatformFeatures instance;


	public abstract void addKeyListener(KeyListener listener);

	public abstract void removeKeyListener(KeyListener listener);

	public static PlatformFeatures getInstance() {
		PlatformFeatures platform = instance;
		if (platform == null) {
			platform = switch (SKDSUtils.OS_TYPE) {
				case WINDOWS -> new WindowsPlatform();
				default ->
						throw new UnsupportedOperationException(SKDSUtils.OS_TYPE + " platform-dependent features are not supported");
			};
			instance = platform;
		}
		return platform;
	}
}
