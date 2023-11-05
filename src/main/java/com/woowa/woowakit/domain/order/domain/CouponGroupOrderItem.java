package com.woowa.woowakit.domain.order.domain;

import java.util.List;

import com.woowa.woowakit.domain.coupon.domain.Coupon;

import lombok.Getter;

@Getter
public class CouponGroupOrderItem {

	private final List<Coupon> coupons;
	private final OrderItem orderItem;

	public CouponGroupOrderItem(final List<Coupon> coupons, final OrderItem orderItem) {
		this.coupons = coupons;
		this.orderItem = orderItem;
	}
}
