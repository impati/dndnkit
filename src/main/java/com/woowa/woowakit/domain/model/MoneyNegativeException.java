package com.woowa.woowakit.domain.model;

import org.springframework.http.HttpStatus;

import com.woowa.woowakit.domain.product.exception.ProductException;

public class MoneyNegativeException extends ProductException {

	public MoneyNegativeException() {
		super("상품 가격은 음수일 수 없습니다.", HttpStatus.NOT_FOUND);
	}
}
