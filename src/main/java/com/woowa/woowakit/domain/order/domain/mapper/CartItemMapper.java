package com.woowa.woowakit.domain.order.domain.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.woowa.woowakit.domain.cart.domain.CartItem;
import com.woowa.woowakit.domain.cart.exception.ProductNotExistException;
import com.woowa.woowakit.domain.order.domain.Order;
import com.woowa.woowakit.domain.order.domain.OrderItem;
import com.woowa.woowakit.domain.product.domain.product.Product;
import com.woowa.woowakit.domain.product.domain.product.ProductRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CartItemMapper {

	private final ProductRepository productRepository;

	public List<CartItem> mapAllFrom(final Order order) {
		return order.getOrderItems()
			.stream()
			.map(orderItem -> mapFrom(orderItem, order.getMemberId()))
			.collect(Collectors.toList());
	}

	private CartItem mapFrom(final OrderItem orderItem, final Long memberId) {
		return CartItem.builder()
			.memberId(memberId)
			.product(getProduct(orderItem.getProductId()))
			.quantity(orderItem.getQuantity().getValue())
			.build();
	}

	private Product getProduct(final Long productId) {
		return productRepository.findById(productId)
			.orElseThrow(ProductNotExistException::new);
	}
}
