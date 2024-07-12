package com.sparta.sensorypeople.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class StatusCommonResponse {
	private HttpStatus httpStatusCode;
	private String message;

	public StatusCommonResponse(HttpStatus httpStatusCode, String message) {
		this.httpStatusCode = httpStatusCode;
		this.message = message;
	}
}