package com.woowa.woowakit.domain.coupon.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class CouponException extends RuntimeException {

	private final HttpStatus httpStatus;

	public CouponException(final String message, final HttpStatus httpStatus) {
		super(message);
		this.httpStatus = httpStatus;
	}
}
