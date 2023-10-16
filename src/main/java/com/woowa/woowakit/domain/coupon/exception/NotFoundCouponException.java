package com.woowa.woowakit.domain.coupon.exception;

import org.springframework.http.HttpStatus;

public class NotFoundCouponException extends CouponException {

	public NotFoundCouponException() {
		super("쿠폰을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST);
	}
}
