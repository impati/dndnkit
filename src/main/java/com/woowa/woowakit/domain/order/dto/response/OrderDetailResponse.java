package com.woowa.woowakit.domain.order.dto.response;

import java.util.List;

import com.woowa.woowakit.domain.order.domain.Order;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class OrderDetailResponse {

	private Long orderId;
	private String orderStatus;
	private Long originTotalPrice;
	private Long totalPrice;
	private String uuid;
	private List<OrderItemDetailResponse> orderItems;

	public static OrderDetailResponse of(final Order order, final List<OrderItemDetailResponse> orderItems) {
		return new OrderDetailResponse(
			order.getId(),
			order.getOrderStatus().name(),
			order.getOriginTotalPrice(),
			order.getTotalPrice(),
			order.getUuid(),
			orderItems
		);
	}
}
