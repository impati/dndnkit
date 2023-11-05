package com.woowa.woowakit.domain.order.domain;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.woowa.woowakit.domain.cart.domain.CartItemRepository;
import com.woowa.woowakit.domain.coupon.domain.Coupon;
import com.woowa.woowakit.domain.order.exception.InvalidCouponException;
import com.woowa.woowakit.domain.order.exception.InvalidOrderItemException;
import com.woowa.woowakit.domain.order.exception.OrderNotFoundException;
import com.woowa.woowakit.domain.order.exception.ProductNotFoundException;
import com.woowa.woowakit.domain.product.domain.Product;
import com.woowa.woowakit.domain.product.domain.ProductRepository;

import io.micrometer.core.annotation.Counted;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderPlaceService {

	private final OrderRepository orderRepository;
	private final CartItemRepository cartItemRepository;
	private final ProductRepository productRepository;

	@Transactional
	@Counted("order.order")
	public void place(
		final Long memberId,
		final Long orderId,
		final List<CouponAppliedOrderItem> couponAppliedOrderItems
	) {
		final Order order = getOrderById(memberId, orderId);
		validateCouponAppliedOrderItems(memberId, order.getOrderItems(), couponAppliedOrderItems);

		order.place();
		order.discount(computeTotalDiscountPrice(couponAppliedOrderItems));
		subtractProductQuantity(order);
		deleteCartItems(order);
	}

	private void validateCouponAppliedOrderItems(
		final Long memberId,
		final List<OrderItem> orderItems,
		final List<CouponAppliedOrderItem> couponAppliedOrderItems
	) {
		for (CouponAppliedOrderItem couponAppliedOrderItem : couponAppliedOrderItems) {
			Coupon coupon = couponAppliedOrderItem.getCoupon();
			OrderItem orderItem = couponAppliedOrderItem.getOrderItem();
			Product product = getProduct(orderItem);
			if (!orderItems.contains(orderItem)) {
				throw new InvalidOrderItemException();
			}
			if (!coupon.isApplicable(product) || !coupon.isOwner(memberId)) {
				throw new InvalidCouponException();
			}
		}
	}

	private int computeTotalDiscountPrice(final List<CouponAppliedOrderItem> couponAppliedOrderItems) {
		return couponAppliedOrderItems.stream()
			.mapToInt(CouponAppliedOrderItem::useCoupon)
			.sum();
	}

	private Product getProduct(final OrderItem orderItem) {
		return productRepository.findById(orderItem.getProductId())
			.orElseThrow(ProductNotFoundException::new);
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
