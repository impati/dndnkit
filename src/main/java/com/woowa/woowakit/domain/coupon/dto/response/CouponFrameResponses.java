package com.woowa.woowakit.domain.coupon.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import com.woowa.woowakit.domain.coupon.domain.CouponFrame;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CouponFrameResponses {

	private List<CouponFrameResponse> couponFrameResponses;

	public static CouponFrameResponses from(final List<CouponFrame> couponFrames) {
		return new CouponFrameResponses(couponFrames.stream()
			.map(CouponFrameResponse::from)
			.collect(Collectors.toList()));
	}
}
