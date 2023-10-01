package com.woowa.woowakit.domain.product.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductBrand {

	FRESH_EASY("프레시지"),
	MOKRAN("이연복의 목란"),
	MYCHEF("마이셰프"),
	SIMPLY_COOK("심플리쿡"),
	COOKIT("국킷"),
	NONE("-");

	private final String name;
}
