package com.woowa.woowakit.domain.product.exception;

import org.springframework.http.HttpStatus;

public class ProductException extends RuntimeException {

	private final HttpStatus httpStatus;

	public ProductException(String message, Throwable cause) {
		super(message, cause);
		this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
	}

	public ProductException(String message, Throwable cause, HttpStatus httpStatus) {
		super(message, cause);
		this.httpStatus = httpStatus;
	}

	public ProductException(String message) {
		super(message);
		this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
	}

	public ProductException(String message, HttpStatus httpStatus) {
		super(message);
		this.httpStatus = httpStatus;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
}
