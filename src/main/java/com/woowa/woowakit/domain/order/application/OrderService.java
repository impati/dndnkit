package com.woowa.woowakit.domain.order.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.woowa.woowakit.domain.auth.domain.AuthPrincipal;
import com.woowa.woowakit.domain.order.domain.Order;
import com.woowa.woowakit.domain.order.domain.OrderPayService;
import com.woowa.woowakit.domain.order.domain.OrderPlaceService;
import com.woowa.woowakit.domain.order.domain.OrderRepository;
import com.woowa.woowakit.domain.order.domain.mapper.OrderMapper;
import com.woowa.woowakit.domain.order.dto.request.OrderCreateRequest;
import com.woowa.woowakit.domain.order.dto.request.OrderPayRequest;
import com.woowa.woowakit.domain.order.dto.request.OrderSearchRequest;
import com.woowa.woowakit.domain.order.dto.response.OrderDetailResponse;
import com.woowa.woowakit.domain.order.dto.response.OrderResponse;
import com.woowa.woowakit.domain.order.exception.OrderNotFoundException;

import io.micrometer.core.annotation.Counted;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

	private final OrderRepository orderRepository;
	private final OrderMapper orderMapper;
	private final OrderPayService orderPayService;
	private final OrderPlaceService orderPlaceService;

	@Transactional(readOnly = true)
	public OrderDetailResponse findOrderByOrderIdAndMemberId(
		final AuthPrincipal authPrincipal,
		final Long orderId
	) {
		final Order order = getOrderByOrderIdAndMemberId(authPrincipal, orderId);

		return OrderDetailResponse.from(order);
	}

	@Transactional(readOnly = true)
	public List<OrderDetailResponse> findAllOrderByMemberId(
		final AuthPrincipal authPrincipal,
		final OrderSearchRequest request
	) {
		final List<Order> orders = orderRepository.findOrdersByMemberId(
			authPrincipal.getId(), request.getLastOrderId(), request.getPageSize()
		);

		return OrderDetailResponse.listOf(orders);
	}

	@Transactional
	@Counted("order.preOrder")
	public OrderResponse create(final AuthPrincipal authPrincipal, final List<OrderCreateRequest> request) {
		final Order order = orderMapper.mapFrom(authPrincipal.getId(), request);

		orderRepository.save(order);
		return OrderResponse.from(order);
	}

	private Order getOrderByOrderIdAndMemberId(
		final AuthPrincipal authPrincipal,
		final Long orderId
	) {
		return orderRepository.findOrderById(orderId, authPrincipal.getId())
			.orElseThrow(OrderNotFoundException::new);
	}

	public void pay(
		final AuthPrincipal authPrincipal,
		final Long orderId,
		final OrderPayRequest request
	) {
		orderPlaceService.place(authPrincipal.getId(), orderId);
		orderPayService.pay(orderId, request.getPaymentKey());
	}
}
