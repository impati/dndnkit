package com.woowa.woowakit.domain.order.domain;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.woowa.woowakit.domain.cart.domain.CartItemRepository;
import com.woowa.woowakit.domain.order.exception.OrderNotFoundException;
import com.woowa.woowakit.domain.product.domain.product.Product;
import com.woowa.woowakit.domain.product.domain.product.ProductRepository;

import io.micrometer.core.annotation.Counted;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderPlaceService {

	private final OrderRepository orderRepository;
	private final CartItemRepository cartItemRepository;
	private final ProductRepository productRepository;

	@Transactional
	@Counted("order.order")
	public void place(final Long memberId, final Long orderId) {
		final Order order = getOrderById(memberId, orderId);

		order.place();
		subtractProductQuantity(order);
		deleteCartItems(order);
	}

	private void deleteCartItems(final Order order) {
		final List<Long> productIds = order.collectProductIds();
		cartItemRepository.deleteAllByProductIdAndMemberId(order.getMemberId(), productIds);
	}

	private void subtractProductQuantity(final Order order) {
		final List<Product> products = productRepository.findByIdsWithPessimistic(order.collectProductIds());
		final QuantityOfProducts quantityData = QuantityOfProducts.from(order);

		for (Product product : products) {
			product.subtractQuantity(quantityData.getFrom(product));
		}
	}

	private Order getOrderById(final Long memberId, final Long orderId) {
		return orderRepository.findByIdAndMemberId(orderId, memberId)
			.orElseThrow(OrderNotFoundException::new);
	}
}
