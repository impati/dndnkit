package com.woowa.woowakit.domain.coupon.dto.response;

import java.time.LocalDate;

import com.woowa.woowakit.domain.coupon.domain.CouponFrame;
import com.woowa.woowakit.domain.coupon.domain.CouponTarget;
import com.woowa.woowakit.domain.coupon.domain.CouponType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponFrameResponse {

	private String name;
	private Long durationDay;
	private LocalDate endDate;
	private CouponType couponType;
	private int minimumOrderAmount;
	private int discount;
	private CouponTarget couponTarget;

	public static CouponFrameResponse from(final CouponFrame couponFrame) {
		return new CouponFrameResponse(
			couponFrame.getName(),
			couponFrame.getDuration().toDays(),
			couponFrame.getEndDate(),
			couponFrame.getCouponType(),
			couponFrame.getMinimumOrderAmount(),
			couponFrame.getDiscount(),
			couponFrame.getCouponTarget()
		);
	}
}
