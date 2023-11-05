package com.woowa.woowakit.domain.order.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import com.woowa.woowakit.domain.coupon.dto.response.CouponResponses;
import com.woowa.woowakit.domain.order.domain.CouponGroupOrderItem;
import com.woowa.woowakit.domain.order.domain.Order;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class OrderResponse {

	private Long id;
	private List<OrderItemResponse> orderItems;
	private String uuid;

	public static OrderResponse of(
		final Order order,
		final List<CouponGroupOrderItem> CouponGroupOrderItems
	) {
		List<OrderItemResponse> orderItemResponses = CouponGroupOrderItems.stream()
			.map(couponOrderItem -> OrderItemResponse.of(
				couponOrderItem.getOrderItem(),
				CouponResponses.from(couponOrderItem.getCoupons())))
			.collect(Collectors.toList());
		return new OrderResponse(order.getId(), orderItemResponses, order.getUuid());
	}
}
