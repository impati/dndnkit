package com.woowa.woowakit.domain.coupon.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import com.woowa.woowakit.domain.coupon.domain.Coupon;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CouponResponses {

	private List<CouponResponse> couponResponses;

	public static CouponResponses from(final List<Coupon> coupons) {
		return new CouponResponses(coupons.stream()
			.map(CouponResponse::from)
			.collect(Collectors.toList()));
	}
}
