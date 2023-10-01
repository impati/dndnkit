package com.woowa.woowakit.domain.product.exception;

import org.springframework.http.HttpStatus;

public class ProductImageException extends ProductException {

	public ProductImageException() {
		super("상품 이미지 형식이 올바르지 않습니다.", HttpStatus.NOT_FOUND);
	}
}
