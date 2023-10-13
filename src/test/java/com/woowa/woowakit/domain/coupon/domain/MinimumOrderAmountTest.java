package com.woowa.woowakit.domain.coupon.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.woowa.woowakit.domain.coupon.exception.MinimumOrderAmountException;

class MinimumOrderAmountTest {

	@ParameterizedTest
	@ValueSource(ints = {1000, 10000})
	@DisplayName("최소 주문 금액 적어도 1000원이상 이어야한다.")
	void createMinimumOrderAmount(final int value) {
		MinimumOrderAmount minimumOrderAmount = MinimumOrderAmount.from(value);

		assertThat(minimumOrderAmount.getValue()).isEqualTo(value);
	}

	@ParameterizedTest
	@ValueSource(ints = {0, 999})
	@DisplayName("최소 주문 금액 1000원 미만이면 최소 주문 금액 생성에 실패한다.")
	void createMinimumOrderAmountFail(final int value) {
		assertThatCode(() -> MinimumOrderAmount.from(value))
			.isInstanceOf(MinimumOrderAmountException.class)
			.hasMessage("최소 주문 금액은 적어도 1000원 이상여야합니다.");
	}
}
