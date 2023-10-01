package com.woowa.woowakit.domain.order.domain.mapper;

import static com.woowa.woowakit.domain.fixture.OrderFixture.*;
import static com.woowa.woowakit.domain.fixture.ProductFixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.woowa.woowakit.domain.cart.domain.CartItem;
import com.woowa.woowakit.domain.order.domain.Order;
import com.woowa.woowakit.domain.order.domain.OrderItem;
import com.woowa.woowakit.domain.product.domain.Product;
import com.woowa.woowakit.domain.product.domain.ProductRepository;
import com.woowa.woowakit.domain.product.domain.ProductStatus;

@SpringBootTest
class CartItemMapperTest {

	@Autowired
	private CartItemMapper cartItemMapper;

	@Autowired
	private ProductRepository productRepository;

	@Test
	@DisplayName("주문으로부터 장바구니 아이템을 가져온다.")
	void mapAllFrom() {
		Product productA = getProduct(1000);
		Product productB = getProduct(2000);
		Order order = getOrder(List.of(
			getOrderItem(productA, 10),
			getOrderItem(productB, 15)
		));

		List<CartItem> cartItems = cartItemMapper.mapAllFrom(order);

		assertThat(cartItems).hasSize(2)
			.extracting(cartItem -> cartItem.getProduct().getId(), CartItem::getQuantity)
			.contains(tuple(productA.getId(), 10L), tuple(productB.getId(), 15L));
	}

	private Order getOrder(final List<OrderItem> orderItems) {
		return getOrderBuilder()
			.orderItems(orderItems)
			.build();
	}

	private Product getProduct(
		final long price
	) {
		return productRepository.save(getInStockProductBuilder()
			.price(price)
			.quantity(100)
			.status(ProductStatus.IN_STOCK)
			.build());
	}

	private OrderItem getOrderItem(final Product product, final long quantity) {
		return getOrderItemBuilder()
			.productId(product.getId())
			.price(product.getPrice())
			.quantity(quantity)
			.build();
	}
}
