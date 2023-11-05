package com.woowa.woowakit.domain.stock.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class StockException extends RuntimeException {

	private final HttpStatus httpStatus;

	public StockException(String message, HttpStatus httpStatus) {
		super(message);
		this.httpStatus = httpStatus;
	}
}
