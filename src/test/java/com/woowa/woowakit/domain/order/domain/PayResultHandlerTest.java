package com.woowa.woowakit.domain.order.domain;

import static com.woowa.woowakit.domain.fixture.OrderFixture.*;
import static com.woowa.woowakit.domain.fixture.ProductFixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.woowa.woowakit.domain.cart.domain.CartItemRepository;
import com.woowa.woowakit.domain.order.exception.OrderNotFoundException;
import com.woowa.woowakit.domain.payment.domain.Payment;
import com.woowa.woowakit.domain.payment.domain.PaymentRepository;
import com.woowa.woowakit.domain.product.domain.Product;
import com.woowa.woowakit.domain.product.domain.ProductRepository;

@SpringBootTest
class PayResultHandlerTest {

	@Autowired
	private PayResultHandler payResultHandler;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CartItemRepository cartItemRepository;

	@Autowired
	private PaymentRepository paymentRepository;

	@Test
	@DisplayName("rollback 시 담은 개수만큼 다시 복구된다.")
	void rollbackProducts() {
		Product productA = getProduct(100, "productA", 10000L);
		Product productB = getProduct(200, "productB", 20000L);
		OrderItem orderItemA = getOrderItem(productA, 10);
		OrderItem orderItemB = getOrderItem(productB, 20);
		Order order = getPersistedOrder(List.of(orderItemA, orderItemB));

		payResultHandler.rollback(order.getId(), new RuntimeException("테스트 용"));

		Order findorder = getOrderById(order.getId());
		Product findProductA = getProductById(productA.getId());
		Product findProductB = getProductById(productB.getId());
		assertThat(findorder.getOrderStatus()).isEqualTo(OrderStatus.CANCELED);
		assertThat(findProductA.getQuantity()).isEqualTo(110);
		assertThat(findProductB.getQuantity()).isEqualTo(220);
		assertThat(cartItemRepository.count()).isEqualTo(2);
	}

	@Test
	@DisplayName("주문 성공 시 결제 완료 상태로 변경하고 결제 정보를 저장한다.")
	void paymentSaveTest() {
		Order order = getPersistedOrder();
		String paymentKey = "paymentKey";

		payResultHandler.save(order.getId(), paymentKey);

		Order findorder = getOrderById(order.getId());
		assertThat(findorder.getOrderStatus()).isEqualTo(OrderStatus.PAYED);
		assertThat(getPayment(order.getId()))
			.extracting(Payment::getTotalPrice, Payment::getPaymentKey, Payment::getOrderId)
			.contains(findorder.getTotalPrice(), paymentKey, findorder.getId());
	}

	private Payment getPayment(final Long orderId) {
		return paymentRepository.findAll().stream()
			.filter(payment -> payment.getOrderId().equals(orderId))
			.findAny()
			.orElseThrow(IllegalStateException::new);
	}

	private Order getOrderById(final Long orderId) {
		return orderRepository.findById(orderId)
			.orElseThrow(OrderNotFoundException::new);
	}

	private Product getProductById(final Long productId) {
		return productRepository.findById(productId)
			.orElseThrow(OrderNotFoundException::new);
	}

	private Order getPersistedOrder() {
		return orderRepository.save(getOrderBuilder().build());
	}

	private Order getPersistedOrder(final List<OrderItem> orderItems) {
		return orderRepository.save(getOrderBuilder()
			.orderItems(orderItems)
			.build());
	}

	private OrderItem getOrderItem(final Product product, final long quantity) {
		return getOrderItemBuilder()
			.productId(product.getId())
			.name(product.getName())
			.quantity(quantity)
			.build();
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
