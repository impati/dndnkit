package com.woowa.woowakit.domain.order.exception;

import org.springframework.http.HttpStatus;

public class InvalidCouponException extends OrderException {

	public InvalidCouponException() {
		super("유효한 쿠폰이 아닙니다.", HttpStatus.FORBIDDEN);
	}
}
