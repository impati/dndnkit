package com.woowa.woowakit.domain.product.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProductCategory {

	WESTERN("양식"),
	JAPANESE("일식"),
	CHINESE("중식"),
	KOREAN("한식"),
	ETC("-");

	private final String name;
}
