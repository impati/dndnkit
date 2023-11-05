package com.woowa.woowakit.domain.order.application;

import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.woowa.woowakit.domain.auth.domain.AuthPrincipal;
import com.woowa.woowakit.domain.coupon.domain.Coupon;
import com.woowa.woowakit.domain.coupon.domain.CouponRepository;
import com.woowa.woowakit.domain.order.domain.Order;
import com.woowa.woowakit.domain.order.domain.OrderItem;
import com.woowa.woowakit.domain.order.domain.OrderRepository;
import com.woowa.woowakit.domain.order.dto.request.OrderSearchRequest;
import com.woowa.woowakit.domain.order.dto.response.OrderDetailResponse;
import com.woowa.woowakit.domain.order.dto.response.OrderItemDetailResponse;
import com.woowa.woowakit.domain.order.dto.response.OrderSimpleResponse;
import com.woowa.woowakit.domain.order.exception.NotFoundCouponException;
import com.woowa.woowakit.domain.order.exception.OrderNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryService {

	private final OrderRepository orderRepository;
	private final CouponRepository couponRepository;

	public OrderDetailResponse findOrderByOrderIdAndMemberId(
		final AuthPrincipal authPrincipal,
		final Long orderId
	) {
		final Order order = getOrderByOrderIdAndMemberId(authPrincipal, orderId);

		return OrderDetailResponse.of(order, convertFrom(order.getOrderItems()));
	}

	private List<OrderItemDetailResponse> convertFrom(final List<OrderItem> orderItems) {
		return orderItems.stream()
			.map(this::convertOrderItemDetailResponse)
			.collect(toList());
	}

	private OrderItemDetailResponse convertOrderItemDetailResponse(final OrderItem orderItem) {
		if (Objects.isNull(orderItem.getCouponId())) {
			return OrderItemDetailResponse.from(orderItem);
		}

		return OrderItemDetailResponse.of(orderItem, getCoupon(orderItem));
	}

	private Coupon getCoupon(final OrderItem orderItem) {
		return couponRepository.findById(orderItem.getCouponId())
			.orElseThrow(NotFoundCouponException::new);
	}

	public List<OrderSimpleResponse> findAllOrderByMemberId(
		final AuthPrincipal authPrincipal,
		final OrderSearchRequest request
	) {
		final List<Order> orders = orderRepository.findOrdersByMemberId(
			authPrincipal.getId(),
			request.getLastOrderId(),
			request.getPageSize()
		);

		return OrderSimpleResponse.from(orders);
	}

	private Order getOrderByOrderIdAndMemberId(
		final AuthPrincipal authPrincipal,
		final Long orderId
	) {
		return orderRepository.findOrderById(orderId, authPrincipal.getId())
			.orElseThrow(OrderNotFoundException::new);
	}
}
