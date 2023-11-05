package com.woowa.woowakit.domain.order.exception;

import org.springframework.http.HttpStatus;

public class NotFoundCouponException extends OrderException {

	public NotFoundCouponException() {
		super("쿠폰을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST);
	}
}
