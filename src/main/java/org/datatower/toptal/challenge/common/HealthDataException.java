package org.datatower.toptal.challenge.common;

import static org.datatower.toptal.challenge.common.HealthDataExceptionCode.BAD_REQUEST;
import static org.datatower.toptal.challenge.common.HealthDataExceptionCode.NOT_FOUND;

public class HealthDataException extends Exception {

	private final HealthDataExceptionCode code;

	public HealthDataException(HealthDataExceptionCode code, String message) {
		super(message);
		this.code = code;
	}

	public HealthDataException(HealthDataExceptionCode code) {
		this.code = code;
	}

	public static HealthDataException badRequest(String message) {
		return new HealthDataException(BAD_REQUEST, message);
	}

	public static HealthDataException notFound(String message) {
		return new HealthDataException(NOT_FOUND, message);
	}

	public HealthDataExceptionCode getCode() {
		return code;
	}
}
