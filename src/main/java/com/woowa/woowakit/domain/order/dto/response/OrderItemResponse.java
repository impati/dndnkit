package com.woowa.woowakit.domain.order.dto.response;

import com.woowa.woowakit.domain.coupon.dto.response.CouponResponses;
import com.woowa.woowakit.domain.order.domain.OrderItem;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class OrderItemResponse {

	private Long id;
	private Long productId;
	private String name;
	private String image;
	private Long price;
	private Long quantity;
	private CouponResponses couponResponses;

	public static OrderItemResponse of(final OrderItem orderItem, final CouponResponses couponResponses) {
		return new OrderItemResponse(
			orderItem.getId(),
			orderItem.getProductId(),
			orderItem.getName(),
			orderItem.getImage(),
			orderItem.getPrice(),
			orderItem.getQuantity().getValue(),
			couponResponses
		);
	}
}
