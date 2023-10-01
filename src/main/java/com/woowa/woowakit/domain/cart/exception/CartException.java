package com.woowa.woowakit.domain.cart.exception;

import org.springframework.http.HttpStatus;

public class CartException extends RuntimeException {

	private final HttpStatus httpStatus;

	public CartException(final String message, final Throwable cause) {
		super(message, cause);
		this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
	}

	public CartException(final String message, final Throwable cause, final HttpStatus httpStatus) {
		super(message, cause);
		this.httpStatus = httpStatus;
	}

	public CartException(final String message) {
		super(message);
		this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
	}

	public CartException(final String message, final HttpStatus httpStatus) {
		super(message);
		this.httpStatus = httpStatus;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
}
