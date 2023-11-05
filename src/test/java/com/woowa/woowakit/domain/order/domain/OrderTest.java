package com.woowa.woowakit.domain.order.domain;

import static com.woowa.woowakit.domain.fixture.OrderFixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Order 도메인 단위 테스트")
class OrderTest {

	@Test
	@DisplayName("Order 가 롤백되면 orderStatus 가 CANCELED 로 변경된다.")
	void rollBackOrder() {
		Order order = getOrder();

		order.cancel();

		assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCELED);
	}

	@Test
	@DisplayName("Order 가 생성되면 orderStatus 가 ORDERED 로 변경된다.")
	void createOrder() {
		Order order = getOrder();

		assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.ORDERED);
	}

	@Test
	@DisplayName("Order 주문이 완료되면 orderStatus 가 PLACED 로 변경된다.")
	void placeOrder() {
		Order order = getOrder();

		order.place();

		assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PLACED);
	}

	@Test
	@DisplayName("Order 결제가 완료되면 orderStatus 가 PAYED 로 변경된다.")
	void playOrder() {
		Order order = getOrder();

		order.pay();

		assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAYED);
	}

	@Test
	@DisplayName("주문 아이템으로부터 주문을 생성하고 주문 아이템의 가격의 합이 주문 총 가격이된다.")
	void calculateTotalPrice() {
		Order order = getOrder(List.of(
			getOrderItem(1000, 1),
			getOrderItem(2000, 2),
			getOrderItem(3000, 3)
		));

		assertThat(order.getTotalPrice()).isEqualTo(14000L);
	}

	@Test
	@DisplayName("주문 아이템으로부터 주문을 생성하고 주문한 상품 ID 를 모두 반환한다.")
	void collectProductIds() {
		Order order = getOrder(List.of(
			getOrderItem(1L),
			getOrderItem(2L),
			getOrderItem(3L)
		));

		assertThat(order.collectProductIds()).contains(1L, 2L, 3L);
	}

	@Test
	@DisplayName("주문 시 총 할인금액을 입력으로 총금액을 계산한다.")
	void orderTotalPriceDiscount() {
		Order order = getOrder(List.of(
			getOrderItem(1000, 1),
			getOrderItem(2000, 2),
			getOrderItem(3000, 3)
		));

		order.discount(5000);

		assertThat(order.getTotalPrice()).isEqualTo(9000);
	}

	private OrderItem getOrderItem(final Long productId) {
		return getOrderItemBuilder()
			.productId(productId)
			.build();
	}

	private OrderItem getOrderItem(final long price, final long quantity) {
		return getOrderItemBuilder()
			.price(price)
			.quantity(quantity)
			.build();
	}

	private Order getOrder(final List<OrderItem> orderItems) {
		return getOrderBuilder()
			.orderItems(orderItems)
			.build();
	}

	private Order getOrder() {
		return getOrderBuilder().build();
	}
}
