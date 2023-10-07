package com.woowa.woowakit.domain.coupon.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.woowa.woowakit.domain.coupon.exception.DiscountRateException;

class DiscountRateTest {

	@ParameterizedTest
	@ValueSource(ints = {10, 25, 100})
	@DisplayName("정률 할인은 정수이며 0보다 크고 100보다 작거나 같아야한다.")
	void createDiscountRate(final int value) {
		DiscountRate discountRate = DiscountRate.from(value);

		assertThat(discountRate.getValue()).isEqualTo(value);
	}

	@ParameterizedTest
	@ValueSource(ints = {0, -10, 101, 1000})
	@DisplayName("정률 할인은 정수이며 0보다 작거나 같은 경우와 100보다 큰 값을 가지면 생성에 실패한다.")
	void createDiscountRateFail(final int value) {
		assertThatCode(() -> DiscountRate.from(value))
			.isInstanceOf(DiscountRateException.class)
			.hasMessage("정률 할인은 정수이며 0보다 크고 100보다 작거나 같아야합니다.");
	}
}
