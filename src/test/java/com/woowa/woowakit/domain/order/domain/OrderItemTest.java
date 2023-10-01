package com.woowa.woowakit.domain.order.domain;

import static com.woowa.woowakit.domain.fixture.OrderFixture.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.woowa.woowakit.domain.model.Money;

class OrderItemTest {

	@Test
	@DisplayName("주문 아이템 수량에 대한 가격 계산")
	void calculateTotalPrice() {
		OrderItem orderItem = getOrderItem(2000, 10);

		assertThat(orderItem.calculateTotalPrice()).isEqualTo(Money.from(20000L));
	}

	private OrderItem getOrderItem(final long price, final long quantity) {
		return getOrderItemBuilder()
			.quantity(quantity)
			.price(price)
			.build();
	}
}
