package com.woowa.woowakit.domain.coupon.exception;

import org.springframework.http.HttpStatus;

public class CouponGroupExpiredException extends CouponException {

	public CouponGroupExpiredException() {
		super("쿠폰틀이 만료되었습니다.", HttpStatus.BAD_REQUEST);
	}
}
