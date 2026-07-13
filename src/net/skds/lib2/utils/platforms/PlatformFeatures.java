package net.skds.lib2.utils.platforms;

import net.skds.lib2.utils.SKDSUtils;
import net.skds.lib2.utils.exception.UnsupportedSystemException;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;

public abstract sealed class PlatformFeatures permits WindowsPlatform {

	private static final class InstanceHolder {
		private static final PlatformFeatures instance = switch (SKDSUtils.OS_TYPE) {
			case WINDOWS -> new WindowsPlatform();
			default ->
					throw new UnsupportedSystemException(SKDSUtils.OS_TYPE + " platform-dependent features are not supported");
		};
	}

	public static PlatformFeatures getInstance() {
		return InstanceHolder.instance;
	}

	public abstract void addKeyListener(KeyListener listener);

	public abstract void removeKeyListener(KeyListener listener);

	public abstract void addMouseListener(MouseListener listener);

	public abstract void removeMouseListener(MouseListener listener);
}
