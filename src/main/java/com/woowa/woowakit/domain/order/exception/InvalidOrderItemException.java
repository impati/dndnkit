package com.woowa.woowakit.domain.order.exception;

import org.springframework.http.HttpStatus;

public class InvalidOrderItemException extends OrderException {

	public InvalidOrderItemException() {
		super("유효하지 않은 주문 아이템입니다.", HttpStatus.FORBIDDEN);
	}
}
