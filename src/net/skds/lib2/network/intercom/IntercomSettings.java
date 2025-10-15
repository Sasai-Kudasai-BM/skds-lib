package net.skds.lib2.network.intercom;

import net.skds.lib2.network.AddressLocation;

public record IntercomSettings(int compressionThreshold, int bundleThreshold, AddressLocation minCompressionLoc) {

	public static final IntercomSettings DEFAULT = new IntercomSettings(128, 6, AddressLocation.WEB);

	public boolean checkCompression(int size, AddressLocation addressLocation) {
		return compressionThreshold >= 0 && size >= compressionThreshold && addressLocation.gEqual(minCompressionLoc);
	}

	public boolean checkBundling(int size) {
		return bundleThreshold >= 0 && size >= bundleThreshold;
	}
}
