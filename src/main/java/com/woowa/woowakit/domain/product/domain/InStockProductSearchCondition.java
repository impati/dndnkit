package com.woowa.woowakit.domain.product.domain;

import java.time.LocalDate;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InStockProductSearchCondition {

	private static final int DEFAULT_PAGE_SIZE = 20;
	private static final LocalDate DEFAULT_SALE_DATE = LocalDate.now().minusDays(1);

	private String productKeyword;
	private Long lastProductId;
	private Long lastProductSale;
	private int pageSize;
	private LocalDate saleDate;

	private InStockProductSearchCondition(final int pageSize, final LocalDate saleDate) {
		this.pageSize = pageSize;
		this.saleDate = saleDate;
	}

	public static InStockProductSearchCondition of(
		final String productKeyword,
		final Long lastProductId,
		final Long lastProductSale,
		final int pageSize,
		final LocalDate saleDate
	) {
		return new InStockProductSearchCondition(productKeyword, lastProductId, lastProductSale, pageSize, saleDate);
	}

	public static InStockProductSearchCondition defaults() {
		return new InStockProductSearchCondition(DEFAULT_PAGE_SIZE, DEFAULT_SALE_DATE);
	}
}
