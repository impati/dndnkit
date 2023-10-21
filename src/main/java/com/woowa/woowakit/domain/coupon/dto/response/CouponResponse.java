package com.woowa.woowakit.domain.coupon.dto.response;

import java.time.LocalDate;

import com.woowa.woowakit.domain.coupon.domain.Coupon;
import com.woowa.woowakit.domain.coupon.domain.CouponTarget;
import com.woowa.woowakit.domain.coupon.domain.CouponType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponResponse {

	private String name;
	private LocalDate expiryDate;
	private CouponType couponType;
	private int minimumOrderAmount;
	private int discount;
	private CouponTarget couponTarget;

	public static CouponResponse from(final Coupon coupon) {
		return new CouponResponse(
			coupon.getName(),
			coupon.getExpiryDate(),
			coupon.getCouponType(),
			coupon.getMinimumOrderAmount(),
			coupon.getDiscount(),
			coupon.getCouponTarget()
		);
	}
}
