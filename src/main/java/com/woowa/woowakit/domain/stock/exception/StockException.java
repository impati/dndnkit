package com.woowa.woowakit.domain.stock.exception;

import org.springframework.http.HttpStatus;

public class StockException extends RuntimeException {

	private final HttpStatus httpStatus;

	public StockException(String message, Throwable cause) {
		super(message, cause);
		this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
	}

	public StockException(String message, Throwable cause, HttpStatus httpStatus) {
		super(message, cause);
		this.httpStatus = httpStatus;
	}

	public StockException(String message) {
		super(message);
		this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
	}

	public StockException(String message, HttpStatus httpStatus) {
		super(message);
		this.httpStatus = httpStatus;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
}
