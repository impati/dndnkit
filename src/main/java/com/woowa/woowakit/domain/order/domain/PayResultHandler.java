package com.woowa.woowakit.domain.order.domain;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.woowa.woowakit.domain.cart.domain.CartItem;
import com.woowa.woowakit.domain.cart.domain.CartItemRepository;
import com.woowa.woowakit.domain.order.domain.mapper.CartItemMapper;
import com.woowa.woowakit.domain.order.exception.OrderNotFoundException;
import com.woowa.woowakit.domain.product.domain.product.Product;
import com.woowa.woowakit.domain.product.domain.product.ProductRepository;

import io.micrometer.core.annotation.Counted;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PayResultHandler {

	private final CartItemRepository cartItemRepository;
	private final ProductRepository productRepository;
	private final OrderRepository orderRepository;
	private final PaymentSaveService paymentSaveService;
	private final CartItemMapper cartItemMapper;

	@Transactional
	@Counted("order.payment.success")
	public void save(final Long orderId, final String paymentKey) {
		final Order order = findOrderById(orderId);

		order.pay();
		paymentSaveService.save(orderId, order.getTotalPrice(), paymentKey);
	}

	@Transactional
	@Counted("order.payment.failure")
	public void rollback(final Long orderId, final Throwable error) {
		final Order order = findOrderById(orderId);

		order.cancel();
		rollbackCartItems(order);
		rollbackProducts(order);
		printLog(error);
	}

	private static void printLog(final Throwable error) {
		log.error("rollback reason = {}", error.getMessage());
	}

	void rollbackProducts(final Order order) {
		final List<Product> products = productRepository.findByIdsWithPessimistic(order.collectProductIds());
		final QuantityOfProducts quantityData = QuantityOfProducts.from(order);

		for (Product product : products) {
			product.addQuantity(quantityData.getFrom(product));
		}
	}

	private void rollbackCartItems(final Order order) {
		List<CartItem> cartItems = cartItemMapper.mapAllFrom(order);
		cartItemRepository.saveAll(cartItems);
	}

	private Order findOrderById(final Long id) {
		return orderRepository.findById(id)
			.orElseThrow(OrderNotFoundException::new);
	}
}
