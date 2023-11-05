package com.woowa.woowakit.domain.order.dto.response;

import static java.util.stream.Collectors.*;

import java.util.List;

import com.woowa.woowakit.domain.order.domain.Order;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class OrderSimpleResponse {

	private Long orderId;
	private String orderStatus;
	private Long originTotalPrice;
	private Long totalPrice;
	private String uuid;

	public static List<OrderSimpleResponse> from(final List<Order> orders) {
		return orders.stream()
			.map(order -> new OrderSimpleResponse(
				order.getId(),
				order.getOrderStatus().name(),
				order.getOriginTotalPrice(),
				order.getTotalPrice(),
				order.getUuid()))
			.collect(toList());
	}
}
