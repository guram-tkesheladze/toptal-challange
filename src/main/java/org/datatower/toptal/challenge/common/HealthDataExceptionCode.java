package org.datatower.toptal.challenge.common;

public enum HealthDataExceptionCode {
	BAD_REQUEST(400),
	NOT_FOUND(404);

	private final int httpStatusCode;

	HealthDataExceptionCode(int httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
	}

	public int getHttpStatusCode() {
		return httpStatusCode;
	}
}
