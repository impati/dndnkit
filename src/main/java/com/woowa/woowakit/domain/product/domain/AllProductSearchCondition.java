package com.woowa.woowakit.domain.product.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AllProductSearchCondition {

	private static final int DEFAULT_PAGE_SIZE = 20;

	private String productKeyword;
	private Long lastProductId;
	private int pageSize;
}
