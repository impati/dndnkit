package com.woowa.woowakit.domain.coupon.exception;

import org.springframework.http.HttpStatus;

public class MinimumOrderAmountException extends CouponException {

	public MinimumOrderAmountException() {
		super("최소 주문 금액은 적어도 1000원 이상여야합니다.", HttpStatus.BAD_REQUEST);
	}
}
