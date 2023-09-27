package com.woowa.woowakit.domain.stock.exception;

import org.springframework.http.HttpStatus;

import com.woowa.woowakit.domain.product.exception.ProductException;

public class StockBatchFailException extends ProductException {

	public StockBatchFailException(Exception e) {
		super("배치 처리 실패", e, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
