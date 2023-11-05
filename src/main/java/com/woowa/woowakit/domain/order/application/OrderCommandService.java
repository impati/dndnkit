package com.woowa.woowakit.domain.order.application;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.woowa.woowakit.domain.auth.domain.AuthPrincipal;
import com.woowa.woowakit.domain.order.domain.CouponApplicableToOrderItemService;
import com.woowa.woowakit.domain.order.domain.CouponAppliedOrderItem;
import com.woowa.woowakit.domain.order.domain.CouponGroupOrderItem;
import com.woowa.woowakit.domain.order.domain.Order;
import com.woowa.woowakit.domain.order.domain.OrderPayService;
import com.woowa.woowakit.domain.order.domain.OrderPlaceService;
import com.woowa.woowakit.domain.order.domain.OrderRepository;
import com.woowa.woowakit.domain.order.domain.mapper.CouponAppliedOrderItemMapper;
import com.woowa.woowakit.domain.order.domain.mapper.OrderMapper;
import com.woowa.woowakit.domain.order.dto.request.OrderCreateRequest;
import com.woowa.woowakit.domain.order.dto.request.OrderPayRequest;
import com.woowa.woowakit.domain.order.dto.response.OrderResponse;

import io.micrometer.core.annotation.Counted;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderCommandService {

	private final OrderRepository orderRepository;
	private final OrderMapper orderMapper;
	private final OrderPayService orderPayService;
	private final OrderPlaceService orderPlaceService;
	private final CouponAppliedOrderItemMapper couponAppliedOrderItemMapper;
	private final CouponApplicableToOrderItemService couponApplicableToOrderItemService;

	@Transactional
	@Counted("order.preOrder")
	public OrderResponse create(
		final AuthPrincipal authPrincipal,
		final List<OrderCreateRequest> request,
		final LocalDate now
	) {
		Order order = orderMapper.mapFrom(authPrincipal.getId(), request);
		orderRepository.save(order);

		List<CouponGroupOrderItem> couponGroupOrderItems = couponApplicableToOrderItemService.getCouponOrderItems(
			authPrincipal.getId(),
			now,
			order.getOrderItems()
		);

		return OrderResponse.of(order, couponGroupOrderItems);
	}

	public void pay(
		final AuthPrincipal authPrincipal,
		final Long orderId,
		final OrderPayRequest request
	) {
		List<CouponAppliedOrderItem> couponAppliedOrderItems = couponAppliedOrderItemMapper.mapFrom(
			authPrincipal.getId(),
			request.getCouponAppliedOrderItems()
		);
		orderPlaceService.place(authPrincipal.getId(), orderId, couponAppliedOrderItems);
		orderPayService.pay(orderId, request.getPaymentKey());
	}
}
