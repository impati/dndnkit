package com.woowa.woowakit.domain.order.exception;

import org.springframework.http.HttpStatus;

public class NotFoundOrderItemException extends OrderException {

	public NotFoundOrderItemException() {
		super("존재하지 않은 주문 아이템 정보입니다.", HttpStatus.BAD_REQUEST);
	}
}
