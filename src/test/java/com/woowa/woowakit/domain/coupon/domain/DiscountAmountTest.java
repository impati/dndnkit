package com.woowa.woowakit.domain.coupon.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.woowa.woowakit.domain.coupon.exception.DiscountAmountException;

class DiscountAmountTest {

	@Test
	@DisplayName("정액 할인 금액은 0보다 크고 최소 주문 금액보다 낮아야한다.")
	void createDiscountAmount() {
		int minimumAmount = 17000;
		int amount = 1000;

		DiscountAmount discountAmount = DiscountAmount.of(amount, minimumAmount);

		assertThat(discountAmount.getValue()).isEqualTo(amount);
	}

	@ParameterizedTest
	@ValueSource(ints = {0, -2000})
	@DisplayName("정액 할인 금액은 0보다 작거나 같은 경우 정액 할인 금액 생성에 실패한다.")
	void createDiscountAmountFailBecauseOfSmallThanZero(final int value) {
		int minimumAmount = 17000;

		assertThatCode(() -> DiscountAmount.of(value, minimumAmount))
			.isInstanceOf(DiscountAmountException.class)
			.hasMessage("정액 할인 금액은 0보다 커야하고 최소 주문 금액보다 낮아야합니다.");
	}

	@Test
	@DisplayName("정액 할인 금액이 최소 주문 금액보다 큰 경우 정액 할인 금액 생성에 실패한다.")
	void createDiscountAmountFailBecauseOfMoreThanMinimumAmount() {
		int minimumAmount = 17000;
		int amount = 18000;

		assertThatCode(() -> DiscountAmount.of(amount, minimumAmount))
			.isInstanceOf(DiscountAmountException.class)
			.hasMessage("정액 할인 금액은 0보다 커야하고 최소 주문 금액보다 낮아야합니다.");
	}
}
