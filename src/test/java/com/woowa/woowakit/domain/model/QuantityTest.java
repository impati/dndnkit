package com.woowa.woowakit.domain.model;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.woowa.woowakit.domain.model.exception.QuantityNegativeException;

@DisplayName("Quantity 도메인 단위 테스트")
class QuantityTest {

	@Test
	@DisplayName("수량은 0보다 작을 수 없다.")
	void negativeProductQuantityError() {
		assertThatCode(() -> Quantity.from(-1L))
			.isInstanceOf(QuantityNegativeException.class)
			.hasMessage("수량은 0보다 작을 수 없습니다.");
	}

	@Test
	@DisplayName("수량의 합을 구할 수 있다.")
	void add() {
		Quantity quantity1 = Quantity.from(1);

		Quantity result = quantity1.add(2);

		assertThat(result).extracting(Quantity::getValue).isEqualTo(3L);
	}
}
