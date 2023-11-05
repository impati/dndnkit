package com.woowa.woowakit.domain.coupon.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CouponTypeTest {

	@Test
	@DisplayName("쿠폰 타입이 RATE 라면 discount 는 0보다 크고 100보다 작거나 같은 수를 가진다.")
	void getDiscountRate() {
		CouponType couponType = CouponType.RATED;

		for (int discount = 1; discount <= 100; discount++) {
			final int finalDiscount = discount;
			assertThatCode(() -> couponType.getDiscount(finalDiscount, 17000))
				.doesNotThrowAnyException();
		}
	}

	@Test
	@DisplayName("쿠폰 타입이 FIXED 라면 discount 는 0보다 크고 최소 주문 금액보다 같거나 낮아야한다.")
	void getDiscountFixed() {
		CouponType couponType = CouponType.FIXED;

		assertThatCode(() -> couponType.getDiscount(15000, 17000))
			.doesNotThrowAnyException();
	}
}
