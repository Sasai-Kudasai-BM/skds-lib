package net.skds.lib2.utils.exception;

import net.skds.lib2.utils.SKDSUtils;

public class UnsupportedSystemException extends UnsupportedOperationException {

	public UnsupportedSystemException() {
		super(SKDSUtils.OS_TYPE + " is not supported there");
	}

	public UnsupportedSystemException(String message) {
		super(message);
	}

	public UnsupportedSystemException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsupportedSystemException(Throwable cause) {
		super(cause);
	}
}
