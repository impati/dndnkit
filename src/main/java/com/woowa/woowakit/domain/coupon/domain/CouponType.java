package com.woowa.woowakit.domain.coupon.domain;

import lombok.Getter;

@Getter
public enum CouponType {

	FIXED,
	RATED;

	public Discount getDiscount(final int discount, final int minimumOrderAmount) {
		if (this == CouponType.FIXED) {
			return DiscountAmount.of(discount, minimumOrderAmount);
		}
		if (this == CouponType.RATED) {
			return DiscountRate.from(discount);
		}

		throw new IllegalStateException();
	}
}
