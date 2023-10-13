package com.woowa.woowakit.domain.coupon.exception;

import org.springframework.http.HttpStatus;

public class NotFoundCouponFrameException extends CouponException {

	public NotFoundCouponFrameException() {
		super("쿠폰틀을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST);
	}
}
