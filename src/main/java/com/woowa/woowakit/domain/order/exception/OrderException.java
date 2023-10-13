package com.woowa.woowakit.domain.order.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class OrderException extends RuntimeException {

	private final HttpStatus httpStatus;

	public OrderException(final String message, final HttpStatus httpStatus) {
		super(message);
		this.httpStatus = httpStatus;
	}
}
