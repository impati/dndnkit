package com.woowa.woowakit.domain.order.domain;

import static com.woowa.woowakit.domain.fixture.OrderFixture.*;
import static com.woowa.woowakit.domain.fixture.ProductFixture.*;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.woowa.woowakit.domain.model.Quantity;
import com.woowa.woowakit.domain.product.domain.product.Product;

class QuantityOfProductsTest {

	@Test
	@DisplayName("주문으로 부터 상품 수량을 가져온다.")
	void getQuantityFromProduct() {
		Product productA = getProduct(1L, 100, "productA", 12000);
		Product productB = getProduct(2L, 200, "productB", 24000);
		Order order = getPersistedOrder(List.of(getOrderItem(productA, 50), getOrderItem(productB, 150)));

		QuantityOfProducts quantityOfProducts = QuantityOfProducts.from(order);

		Assertions.assertThat(quantityOfProducts.getFrom(productA)).isEqualTo(Quantity.from(50));
		Assertions.assertThat(quantityOfProducts.getFrom(productB)).isEqualTo(Quantity.from(150));
	}

	private Order getPersistedOrder(final List<OrderItem> orderItems) {
		return getOrderBuilder()
			.orderItems(orderItems)
			.build();
	}

	private OrderItem getOrderItem(final Product product, final long quantity) {
		return getOrderItemBuilder()
			.productId(product.getId())
			.name(product.getName())
			.quantity(quantity)
			.build();
	}

	private Product getProduct(
		final long id,
		final long quantity,
		final String name,
		final long price
	) {
		return getProductBuilder()
			.id(id)
			.quantity(quantity)
			.name(name)
			.price(price)
			.build();
	}
}
