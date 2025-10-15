package net.skds.lib2.security;

import net.skds.lib2.utils.ArrayUtils;

public record AuthorizationData(String type, byte[] data) {
	public static final AuthorizationData TRUSTED = new AuthorizationData("trusted", ArrayUtils.EMPTY_BYTE);
}
