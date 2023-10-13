package com.woowa.woowakit.domain.coupon.exception;

import org.springframework.http.HttpStatus;

public class DiscountRateException extends CouponException {

	public DiscountRateException() {
		super("정률 할인은 정수이며 0보다 크고 100보다 작거나 같아야합니다.", HttpStatus.BAD_REQUEST);
	}
}
