package com.woowa.woowakit.domain.order.domain;

import static com.woowa.woowakit.domain.fixture.CartItemFixture.*;
import static com.woowa.woowakit.domain.fixture.OrderFixture.*;
import static com.woowa.woowakit.domain.fixture.ProductFixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.woowa.woowakit.domain.cart.domain.CartItemRepository;
import com.woowa.woowakit.domain.order.exception.OrderNotFoundException;
import com.woowa.woowakit.domain.product.domain.Product;
import com.woowa.woowakit.domain.product.domain.ProductRepository;

@SpringBootTest
class OrderPlaceServiceTest {

	@Autowired
	private OrderPlaceService orderPlaceService;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CartItemRepository cartItemRepository;

	@Test
	@DisplayName("주문시 주문 상태가 PLACE 가 되고 재고 감소와 장바구니를 비운다.")
	void place() {
		Long memberId = 99L;
		Product productA = getProduct(100, "ProductA", 15000);
		Product productB = getProduct(200, "ProductB", 30000);
		addCartItem(memberId, productA, 90);
		addCartItem(memberId, productB, 110);
		Order order = getPersistedOrder(memberId);

		orderPlaceService.place(memberId, order.getId());

		assertThat(getOrderById(order.getId()).getOrderStatus()).isEqualTo(OrderStatus.PLACED);
		assertThat(getProductById(productA.getId()).getQuantity()).isEqualTo(10);
		assertThat(getProductById(productB.getId()).getQuantity()).isEqualTo(90);
		assertThat(cartItemRepository.findCartItemByMemberId(memberId)).isEmpty();
	}

	private Order getOrderById(final Long orderId) {
		return orderRepository.findById(orderId)
			.orElseThrow(OrderNotFoundException::new);
	}

	private Product getProductById(final Long productId) {
		return productRepository.findById(productId)
			.orElseThrow(OrderNotFoundException::new);
	}

	private void addCartItem(
		final Long memberId,
		final Product product,
		final long quantity
	) {
		cartItemRepository.save(getCartItemBuilder()
			.memberId(memberId)
			.product(product)
			.quantity(quantity)
			.build());
	}

	private Order getPersistedOrder(final Long memberId) {
		List<OrderItem> orderItems = cartItemRepository.findCartItemByMemberId(memberId).stream()
			.map(cartItemSpecification -> getOrderItemBuilder()
				.productId(cartItemSpecification.getProductId())
				.quantity(cartItemSpecification.getQuantity())
				.build())
			.collect(Collectors.toList());
		return orderRepository.save(getOrderBuilder()
			.memberId(memberId)
			.orderItems(orderItems)
			.build());
	}

	private Product getProduct(
		final long quantity,
		final String name,
		final long price
	) {
		return productRepository.save(getInStockProductBuilder()
			.quantity(quantity)
			.name(name)
			.price(price)
			.build());
	}
}
