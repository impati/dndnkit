package com.woowa.woowakit.domain.model.exception;

import org.springframework.http.HttpStatus;

public class QuantityNegativeException extends ModelException {

	public QuantityNegativeException() {
		super("수량은 0보다 작을 수 없습니다.", HttpStatus.BAD_REQUEST);
	}
}
