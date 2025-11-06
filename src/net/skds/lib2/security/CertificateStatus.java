package net.skds.lib2.security;

public enum CertificateStatus {

	VALID, NOT_VALID_YET, EXPIRED, REVOKED, INVALID;

	public boolean isOk() {
		return this == VALID;
	}
}
