package com.sparta.sensorypeople.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DataCommonResponse<T> {
	private HttpStatus httpStatusCode;
	private String message;
	private T data;

	public DataCommonResponse(HttpStatus httpStatusCode, String message, T data) {
		this.httpStatusCode = httpStatusCode;
		this.message = message;
		this.data = data;
	}
}