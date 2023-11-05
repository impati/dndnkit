package com.woowa.woowakit.domain.order.domain;

import com.woowa.woowakit.domain.coupon.domain.Coupon;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponAppliedOrderItem {

	private final Coupon coupon;
	private final OrderItem orderItem;

	public Integer useCoupon() {
		orderItem.applyCoupon(coupon.getId());

		coupon.used();
		return coupon.computeDiscountPrice(orderItem.getPrice());
	}

	public static CouponAppliedOrderItem of(final Coupon coupon, final OrderItem orderItem) {
		return new CouponAppliedOrderItem(coupon, orderItem);
	}
}
