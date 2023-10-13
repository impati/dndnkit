package com.woowa.woowakit.domain.coupon.exception;

import org.springframework.http.HttpStatus;

public class DiscountAmountException extends CouponException {

	public DiscountAmountException() {
		super("정액 할인 금액은 0보다 커야하고 최소 주문 금액보다 낮아야합니다.", HttpStatus.BAD_REQUEST);
	}
}
