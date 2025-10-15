package net.skds.lib2.network.intercom;

import lombok.AllArgsConstructor;
import net.skds.lib2.network.AddressLocation;
import net.skds.lib2.utils.EnumHelper;

@AllArgsConstructor
public enum SecurityLevel implements EnumHelper<SecurityLevel> {
	LOCALHOST(false, false),
	LAN_NO_ENCRYPTION(false, false),
	LAN_SAFE(true, false),
	WEB_SAFE(true, true);

	//public final boolean requireAuth;
	public final boolean useEncryption;
	public final boolean checkCertificate;

	public static SecurityLevel getOptimal(AddressLocation location) {
		return switch (location) {
			case LOCALHOST -> LOCALHOST;
			case LAN -> LAN_SAFE;
			default -> WEB_SAFE;
		};
	}
}
