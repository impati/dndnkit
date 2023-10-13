package com.woowa.woowakit.domain.model.exception;

import org.springframework.http.HttpStatus;

public class MoneyNegativeException extends ModelException {

	public MoneyNegativeException() {
		super("상품 가격은 음수일 수 없습니다.", HttpStatus.NOT_FOUND);
	}
}
