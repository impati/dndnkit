package com.woowa.woowakit.domain.fixture;

import java.util.List;

import com.woowa.woowakit.domain.order.domain.Order;
import com.woowa.woowakit.domain.order.domain.Order.OrderBuilder;
import com.woowa.woowakit.domain.order.domain.OrderItem;
import com.woowa.woowakit.domain.order.domain.OrderItem.OrderItemBuilder;

public class OrderFixture {

	public static OrderItemBuilder getOrderItemBuilder() {
		return OrderItem.builder()
			.image("image")
			.name("name")
			.price(1000L)
			.productId(1L)
			.quantity(1L);
	}

	public static OrderBuilder getOrderBuilder() {
		return Order.builder()
			.memberId(1L)
			.orderItems(List.of(getOrderItemBuilder().build(), getOrderItemBuilder()
				.productId(2L)
				.name("해산물 밀키트")
				.price(2000L)
				.build()));
	}
}
