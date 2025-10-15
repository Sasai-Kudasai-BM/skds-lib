package net.skds.lib2.network.intercom;

public record SecurityOptions(SecurityLevel minimumSecurityLevel, boolean forceEncryption, boolean forceCertificate) {

	public static final SecurityOptions DEFAULT = new SecurityOptions(SecurityLevel.LOCALHOST, false, false);
}
