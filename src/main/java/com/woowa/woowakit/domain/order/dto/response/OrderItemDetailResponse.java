package com.woowa.woowakit.domain.order.dto.response;

import com.woowa.woowakit.domain.coupon.domain.Coupon;
import com.woowa.woowakit.domain.coupon.dto.response.CouponResponse;
import com.woowa.woowakit.domain.order.domain.OrderItem;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class OrderItemDetailResponse {

	private Long id;
	private Long productId;
	private String name;
	private String image;
	private Long price;
	private Long quantity;
	private boolean appliedCoupon;
	private CouponResponse couponResponse;

	private OrderItemDetailResponse(
		final Long id,
		final Long productId,
		final String name,
		final String image,
		final Long price,
		final Long quantity) {
		this.id = id;
		this.productId = productId;
		this.name = name;
		this.image = image;
		this.price = price;
		this.quantity = quantity;
		this.appliedCoupon = false;
	}

	public static OrderItemDetailResponse of(final OrderItem orderItem, final Coupon coupon) {
		return new OrderItemDetailResponse(
			orderItem.getId(),
			orderItem.getProductId(),
			orderItem.getName(),
			orderItem.getImage(),
			orderItem.getPrice(),
			orderItem.getQuantity().getValue(),
			true,
			CouponResponse.from(coupon)
		);
	}

	public static OrderItemDetailResponse from(final OrderItem orderItem) {
		return new OrderItemDetailResponse(
			orderItem.getId(),
			orderItem.getProductId(),
			orderItem.getName(),
			orderItem.getImage(),
			orderItem.getPrice(),
			orderItem.getQuantity().getValue()
		);
	}
}
