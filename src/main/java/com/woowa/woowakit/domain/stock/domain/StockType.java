package com.woowa.woowakit.domain.stock.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StockType {

	NORMAL("정상"), EXPIRED("만료"), PROCESSED("재고 처리됨");

	private final String name;
}
